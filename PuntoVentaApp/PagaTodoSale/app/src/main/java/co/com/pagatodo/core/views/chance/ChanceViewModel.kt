package co.com.pagatodo.core.views.chance

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.interactors.IChanceInteractor
import co.com.pagatodo.core.data.interactors.ILocalInteractor
import co.com.pagatodo.core.data.interactors.IUtilInteractor
import co.com.pagatodo.core.data.model.ChanceModel
import co.com.pagatodo.core.data.model.ProductModel
import co.com.pagatodo.core.data.model.RepeatChanceModel
import co.com.pagatodo.core.data.model.request.RequestChanceModel
import co.com.pagatodo.core.data.print.IPrinterStatusInfo
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.*
import co.com.pagatodo.core.util.CURRENT_CHANCE_BET
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.views.SingleLiveEvent
import co.com.pagatodo.core.views.base.BaseEvents
import co.com.pagatodo.core.views.base.BaseObservableViewModel
import co.com.pagatodo.core.views.base.BaseViewModel
import co.com.pagatodo.core.views.base.removeLastTransaction
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * El viewmodel es el encargado de realizar los llamados al interactor y manejar los datos del producto
 * este le comunica a la interfaz cuando cambia la información por medio de observables
 */

@Singleton
class ChanceViewModel: BaseViewModel() {

    /**
     * En esta clase se definen los eventos encargados de informar a la actividad las respuestas de los servicios
     * y cuando cambia la información del producto
     */
    sealed class ViewEvent {
        class PayError(val errorMessage: String): ViewEvent()
        class PaySuccess(val successMessage: String): ViewEvent()
        class ChanceProductInfo(val productModel: ProductModel): ViewEvent()
    }

    // Inyección de las variables necesarias por medio de dagger
    @Inject
    lateinit var utilInteractor: IUtilInteractor
    @Inject
    lateinit var chanceInteractor: IChanceInteractor
    @Inject
    lateinit var localInteractor: ILocalInteractor
    @Inject
    lateinit var printerStatusInfo: IPrinterStatusInfo

    // Varible observarda en la interfaz al momento de generar un número aleatorio
    lateinit var randomNumberLiveData: MutableLiveData<List<String>>
    var singleLiveEvent: SingleLiveEvent<ViewEvent> = SingleLiveEvent()

    /**
     * Inicializacion del viewmodel
     */
    init {
        //Inicializar el componente de dagger para realizar la inyección de dependencias
        DaggerChanceComponent.builder()
            .utilModule(UtilModule())
            .chanceModule(ChanceModule())
            .printerStatusModule(PrinterStatusModule())
            .localModule(LocalModule())
            .build().inject(this)

        if(!::randomNumberLiveData.isInitialized) {
            randomNumberLiveData = MutableLiveData()
        }
    }

    /**
     * Método utilizado para cargar la información del producto
     */
    @SuppressLint("CheckResult")
    fun fetchChanceProduct() {
        localInteractor.getProductInfo(R_string(R.string.code_chance)).subscribe ({
            //Cuando se recibe alguna respuesta esta es comunicada al singleLiveEvente
            //el cual a su vez le comunica a la interfaz la respuesta obtenida
            singleLiveEvent.value = ViewEvent.ChanceProductInfo(it)
        },{
            //En caso de que haya un error en la respuesta se le comunica a la interfaz de dicho error
            if (it is ConnectException){
                singleLiveEvent.value = ViewEvent.PayError(R_string(R.string.message_no_network))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.value = ViewEvent.PayError(
                     R_string(R.string.message_error_transaction)
                )
            }
        })
    }

    /**
     * Método utilizado para generar un número aleatorio
     */
    @SuppressLint("CheckResult")
    fun dispatchGenerateRandom(quantityNumber: Int, quantityDigits: Int) {
        utilInteractor.getRandomNumbers(quantityNumber, quantityDigits)?.subscribe ({ data ->
            //Cuando se genera el número aleatorio este se asigna a la variable randomNumberLiveData
            //Quien a su vez es escuchada en la interfaz y se muestra el número aleatorio
            randomNumberLiveData.value = data
        },{
            if (it is ConnectException){
                singleLiveEvent.value = ViewEvent.PayError(R_string(R.string.message_no_network))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.value = ViewEvent.PayError(
                     R_string(R.string.message_error_transaction)
                )
            }
        })
    }

    /**
     * Método utilizado para validar el estado de la impresora y realizar la apuesta de chance
     */
    fun dispatchPayChance(request: RequestChanceModel, isRetry: Boolean = true, transactionType: String? = null) {
        //Antes de realizar la apuesta se valida el estado de la impresora
        //en caso de estar funcionando se realiza la apuesta
        validatePrinterStatus(printerStatusInfo){
            executePayChance(request, isRetry, transactionType)
        }
    }

    /**
     * Método que realiza la apuesta de chance
     */
    @SuppressLint("CheckResult")
    fun executePayChance(request: RequestChanceModel, isRetry: Boolean = true, transactionType: String? = null){
        //Una vez es llamado este metodo se almacena en local el request, esto para en caso de que falle la transacción
        //queda almacenada para usar en los reintentos

        val chances = request.chances ?: arrayListOf()
        val lotteries = request.lotteries ?: arrayListOf()

        // El interactor realiza el llamado de la transacción y por medio de un observable recibe la respuesta de ella
        // la cual es comunicada a la interfaz en caso de ser exitosa o fallida
        chanceInteractor
            .payChance(chances, lotteries, request.valueWithoutIva, request.suggestedValue, isRetry, transactionType, request.transactionTime, request.sequenceTransaction)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe ({ response ->
                // Una vez se recibe la respuesta, la transacción es eliminada

                if (response.isSuccess) {
                    val uniqueSerial = response.uniqueSerial ?: "N/A"
                    val digitChecking = response.digitChecking ?: "N/A"
                    val totalValueBetting = response.totalValueBetting ?: 0.0
                    val totalValuePaid = response.totalValuePaid ?: 0.0
                    val totalValueOverloaded = response.totalValueOverloaded ?: 0.0
                    val totalValueIva = response.totalValueIva ?: 0.0
                    val listChancePrint = mutableListOf<ChanceModel>()

                    // Si la respuesta es exitosa se hace un mapeo de los datos que retorna el servicio
                    // esto para realizar la impresión de acorde a la información retornada

                    val modalityValuesID = response.answerBets?.map { it.idGame }?.groupBy { it}?.map { it.key }

                    modalityValuesID?.forEach { idGame->

                        val chanceModel = ChanceModel().apply {
                            this.number = "0"
                            this.direct = "0"
                            this.combined = "0"
                            this.paw = "0"
                            this.nail = "0"
                        }


                        val modalityValues = response.answerBets?.filter { it.idGame == idGame}

                        modalityValues?.forEachIndexed { _, betsAnswer ->
                            if (betsAnswer.modalityCode.equals("01") || betsAnswer.modalityCode.equals("02")){
                                chanceModel.direct = betsAnswer.valueBet.toString()
                               if(betsAnswer.originalNumber != null)
                                   chanceModel.number = betsAnswer.originalNumber
                            }else if (betsAnswer.modalityCode.equals("03") || betsAnswer.modalityCode.equals("04")){
                                chanceModel.combined = betsAnswer.valueBet.toString()
                                if(betsAnswer.originalNumber != null)
                                    chanceModel.number = betsAnswer.originalNumber
                            }else if (betsAnswer.modalityCode.equals("05")){
                                chanceModel.paw = betsAnswer.valueBet.toString()
                                if(betsAnswer.originalNumber != null)
                                    chanceModel.number = betsAnswer.originalNumber
                            }else if (betsAnswer.modalityCode.equals("06")){
                                chanceModel.nail = betsAnswer.valueBet.toString()
                                if(betsAnswer.originalNumber != null)
                                    chanceModel.number = betsAnswer.originalNumber
                            }
                        }
                        listChancePrint.add(chanceModel)


                    }
                    
                    // Para poder repetir la apuesta, esta actual apuesta es almacenada en la memoria ram
                    // en caso de que se cierre el app esta apuesta es eliminada
                    CURRENT_CHANCE_BET = RepeatChanceModel().apply {
                        this.chances = chances
                        this.lotteries = lotteries
                        this.valueWithoutIva = request.valueWithoutIva
                        this.suggestedValue = request.suggestedValue
                        this.raffleDate = request.raffleDate
                        this.stubs = request.stubs
                    }

                    // Se actualiza la colilla actual con la nueva colilla retornada por el servicio
                    StorageUtil.updateStub(response.serie1 ?: "", response.currentSerie2 ?: "")
                    // Se envía esta información al interactor para realizar la impresión

                    val dateDefault = DateUtil.convertDateToStringFormat(DateUtil.StringFormat.DDMMYY,Date())
                    val hourDefault = DateUtil.convertDateToStringFormat(DateUtil.StringFormat.HHMMSS,Date())

                    val dateGame = DateUtil.convertDateToStringFormat(DateUtil.StringFormat.DDMMYYYY_SPLIT_DASH, ( DateUtil.convertStringToDateFormat(DateUtil.StringFormat.DDMMYY,response.date?:dateDefault)))

                    chanceInteractor.print(totalValueBetting, totalValuePaid, totalValueOverloaded, totalValueIva, dateGame,response.hour?:hourDefault, lotteries, listChancePrint, uniqueSerial, digitChecking, request.stubs, request.maxRows?:5,transactionType != null){
                        if (it == PrinterStatus.OK){

                            BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", R_string(R.string.message_success_bet), true))

                        }else{
                            BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", R_string(R.string.message_error_print_device)))
                        }
                    }
                }
                else {
                    // En caso de que no sea exitosa la transaccion se le informa a la interfaz
                    removeLastTransaction()
                    BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", response.message ?: R_string(R.string.message_error_transaction)))
                }

            },{
                // En caso de que no sea exitosa la transaccion se le informa a la interfaz y se elimina la transacción
                removeLastTransaction(it)
                if (it is ConnectException){
                    singleLiveEvent.value = ViewEvent.PayError(R_string(R.string.message_no_network))
                } else if (it !is SocketTimeoutException) {
                    singleLiveEvent.value = ViewEvent.PayError(
                         R_string(R.string.message_error_transaction)
                    )
                }
            })
    }

}