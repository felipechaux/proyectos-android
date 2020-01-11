package co.com.pagatodo.core.views.paymillionaire

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.interactors.ILocalInteractor
import co.com.pagatodo.core.data.interactors.IPayMillionaireInteractor
import co.com.pagatodo.core.data.interactors.IUtilInteractor
import co.com.pagatodo.core.data.model.*
import co.com.pagatodo.core.data.model.request.RequestPayMillonaireModel
import co.com.pagatodo.core.data.print.IPrinterStatusInfo
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.*
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.views.SingleLiveEvent
import co.com.pagatodo.core.views.base.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PayMillionaireViewModel: BaseViewModel() {

    sealed class ViewEvent {

        enum class RandomNumberType {
            Three, Four, Five
        }

        class ShowMode(val modes: List<ModeValueModel>): ViewEvent()
        class PayError(val errorMessage: String): ViewEvent()
        class PaySuccess(val successMessage: String): ViewEvent()
        class RandomNumber(val numbers: List<String>, val randomType: RandomNumberType): ViewEvent()
    }

    var singleLiveEvent: SingleLiveEvent<ViewEvent> = SingleLiveEvent()
    var productModelLiveData = MutableLiveData<ProductModel>()
    var modalities: List<ModeValueModel> = arrayListOf()

    @Inject
    lateinit var payMillionaireInteractor: IPayMillionaireInteractor
    @Inject
    lateinit var utilInteractor: IUtilInteractor
    @Inject
    lateinit var localInteractor: ILocalInteractor
    @Inject
    lateinit var printerStatusInfo: IPrinterStatusInfo

    init {
        DaggerPayMillionaireComponent
            .builder()
            .payMillionaireModule(PayMillionaireModule())
            .utilModule(UtilModule())
            .localModule(LocalModule())
            .printerStatusModule(PrinterStatusModule())
            .build().inject(this)
    }

    @SuppressLint("CheckResult")
    fun fetchProduct() {
        localInteractor.getProductInfo(R_string(R.string.code_paymillionaire)).subscribe ({
            productModelLiveData.value = it
        },{
            if (it is ConnectException){
                singleLiveEvent.value = ViewEvent.PayError(R_string(R.string.message_no_network))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.value =
                        ViewEvent.PayError(
                             R_string(R.string.message_error_transaction)
                        )
            }
        })
    }

    @SuppressLint("CheckResult")
    fun dispatchMode() {
        localInteractor.getPayMillionaireModes()?.subscribe ({
            modalities = it
            singleLiveEvent.postValue(ViewEvent.ShowMode(it))
        },{
            if (it is ConnectException){
                singleLiveEvent.postValue(ViewEvent.PayError(R_string(R.string.message_no_network)))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.postValue(
                    ViewEvent.PayError( R_string(R.string.message_error_transaction))
                )
            }
        })
    }

    @SuppressLint("CheckResult")
    fun dispatchGenerateRandom(quantityNumber: Int, quantityDigits: Int, randomType: ViewEvent.RandomNumberType) {
        utilInteractor.getRandomNumbers(quantityNumber, quantityDigits)?.subscribe ({ data ->
            singleLiveEvent.value =
                ViewEvent.RandomNumber(
                    data,
                    randomType
                )
        },{
            if (it is ConnectException){
                singleLiveEvent.value = ViewEvent.PayError(R_string(R.string.message_no_network))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.value =
                        ViewEvent.PayError(
                             R_string(R.string.message_error_transaction)
                        )
            }
        })
    }

    @SuppressLint("CheckResult")
    fun executePayment(request: RequestPayMillonaireModel, isRetry: Boolean = true, transactionType: String? = null) {

        if (transactionType == null)
            saveLastTransaction(request, ProductName.PAYMILLIONAIRE.rawValue)

        val chances = request.chances ?: arrayListOf()
        val lotteries = request.lotteries ?: arrayListOf()

        payMillionaireInteractor
            .payPayMillionaire(chances, lotteries, request.valueWithoutIva, isRetry, transactionType, request.transactionTime, request.sequenceTransaction)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(
                { response ->

                    if (response.isSuccess) {
                        StorageUtil.updateSequenceTransaction()
                        val uniqueSerial = response.uniqueSerial ?: "N/A"
                        val digitChecking = response.digitChecking ?: "N/A"
                        val totalValueBetting = response.totalValueBetting ?: 0.0
                        val totalValuePaid = response.totalValuePaid ?: 0.0
                        val totalValueIva = response.totalValueIva ?: 0.0
                        val productModel = request.product ?: ProductModel()
                        val selectedModeValue = request.selectedValue ?: ModeValueModel()

                        StorageUtil.updateStub(response.serie1 ?: "", response.currentSerie2 ?: "")

                        val dateGame = "${DateUtil.convertDateToStringFormat(
                            DateUtil.StringFormat.DDMMYYYY_SPLIT_DASH, ( DateUtil.convertStringToDateFormat(
                                DateUtil.StringFormat.DDMMYY,response.date!!)))}|${response.hour}|${request.raffleDate.replace('/', '-')}"

                        payMillionaireInteractor.print(totalValueBetting, totalValuePaid, totalValueIva, dateGame,
                            lotteries, chances, uniqueSerial, digitChecking, productModel, selectedModeValue, request.stubs, transactionType != null) {
                            if (it == PrinterStatus.OK){
                                BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", R_string(R.string.message_success_bet), true))
                            }else{
                                BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", R_string(R.string.message_error_print_device)))
                            }
                        }
                    }
                    else {
                        removeLastTransaction()
                        BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", response.message ?: R_string(R.string.message_error_transaction)))
                    }
                }, {
                    removeLastTransaction(it)
                    if (it is ConnectException){
                        BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", R_string(R.string.message_no_network)))
                    } else if (it !is SocketTimeoutException) {
                        BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("",  R_string(R.string.message_error_transaction)))
                    }
                }
            )
    }

    @SuppressLint("CheckResult")
    fun dispatchPayMillionaire(request: RequestPayMillonaireModel, isRetry: Boolean = true, transactionType: String? = null) {
        validatePrinterStatus(printerStatusInfo) {
            executePayment(request, isRetry, transactionType)
        }
    }
}