package co.com.pagatodo.core.views.superastro

import android.annotation.SuppressLint
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.interactors.IUtilInteractor
import co.com.pagatodo.core.data.interactors.SuperAstroInteractor
import co.com.pagatodo.core.data.model.*
import co.com.pagatodo.core.data.model.print.SuperAstroPrintModel
import co.com.pagatodo.core.data.model.request.RequestSuperAstroModel
import co.com.pagatodo.core.data.print.IPrinterStatusInfo
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.DaggerSuperAstroComponent
import co.com.pagatodo.core.di.PrinterStatusModule
import co.com.pagatodo.core.di.SuperAstroModule
import co.com.pagatodo.core.util.CURRENT_SUPERASTRO_BET
import co.com.pagatodo.core.util.CurrencyUtils
import co.com.pagatodo.core.util.DeviceUtil
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
class SuperAstroViewModel: BaseViewModel() {

    sealed class ViewEvent {
        class ResponseError(val errorMessage: String?): ViewEvent()
        class ResponseSuccess(val successMessage: String?): ViewEvent()
        class ResponseRandomNumber(val numbers: List<String>): ViewEvent()
        class ResponseKeyValueParameters(val parameters: List<KeyValueModel>): ViewEvent()
        class ResponseModalities(val modalities: List<ModalityModel>): ViewEvent()
    }

    @Inject
    lateinit var superAstroInteractor: SuperAstroInteractor
    @Inject
    lateinit var utilInteractor: IUtilInteractor
    @Inject
    lateinit var printerStatusInfo: IPrinterStatusInfo

    lateinit var ticketHeader: String
    lateinit var ticketFooter: String
    lateinit var ticketPrizePlan: String

    init {
        DaggerSuperAstroComponent
            .builder()
            .superAstroModule(SuperAstroModule())
            .printerStatusModule(PrinterStatusModule())
            .build().inject(this)
    }

    var singleLiveEvent: SingleLiveEvent<ViewEvent> =
        SingleLiveEvent()

    fun loadProductParameters() {
        getProductParameters()
    }

    fun loadModalities() {
        getModalities()
    }

    fun loadRandomNumbers(quantityNumber: Int, quantityDigits: Int) {
        getRandomNumbers(quantityNumber, quantityDigits)
    }
}

@SuppressLint("CheckResult")
private fun SuperAstroViewModel.getProductParameters() {
    superAstroInteractor.getProductParametersForSuperAstro()?.subscribe ({
        it.forEach{
            if(it.key.equals(R_string(R.string.name_column_header))) {
                ticketHeader = it.value ?: ""
            }
            if(it.key.equals(R_string(R.string.name_column_footer))) {
                ticketFooter = it.value ?: ""
            }
            if(it.key.equals(R_string(R.string.name_column_plan_prize))) {
                ticketPrizePlan = it.value ?: ""
            }
        }
        singleLiveEvent.value =
            SuperAstroViewModel.ViewEvent.ResponseKeyValueParameters(it)
    },{
        if (it is ConnectException){
            singleLiveEvent.value = SuperAstroViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
        } else if (it !is SocketTimeoutException) {
            singleLiveEvent.value =
                    SuperAstroViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
        }
    })
}

@SuppressLint("CheckResult")
private fun SuperAstroViewModel.getModalities() {
    superAstroInteractor.getModalities()?.subscribe ({
        singleLiveEvent.value = SuperAstroViewModel.ViewEvent.ResponseModalities(it)
    },{
        if (it is ConnectException){
            singleLiveEvent.value = SuperAstroViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
        } else if (it !is SocketTimeoutException) {
            singleLiveEvent.value =
                    SuperAstroViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
        }
    })
}

@SuppressLint("CheckResult")
private fun SuperAstroViewModel.getRandomNumbers(quantityNumber: Int, quantityDigits: Int) {
    utilInteractor.getRandomNumbers(quantityNumber, quantityDigits)?.subscribe ({
        singleLiveEvent.value =
            SuperAstroViewModel.ViewEvent.ResponseRandomNumber(it)
    },{
        if (it is ConnectException){
            singleLiveEvent.value = SuperAstroViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
        } else if (it !is SocketTimeoutException) {
            singleLiveEvent.value =
                    SuperAstroViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
        }
    })
}

fun SuperAstroViewModel.paySuperAstro(request: RequestSuperAstroModel, isRetry: Boolean = true, transactionType: String? = null) {
    validatePrinterStatus(printerStatusInfo){
        executePaySuperAstro(request, isRetry, transactionType)
    }
}

@SuppressLint("CheckResult")
private fun SuperAstroViewModel.executePaySuperAstro(request: RequestSuperAstroModel, isRetry: Boolean = true, transactionType: String? = null){
    val superastroListModel = request.superastroList ?: arrayListOf()
    val lotteries = request.lotteries ?: arrayListOf()

    if (transactionType == null)
        saveLastTransaction(request, ProductName.SUPER_ASTRO.rawValue)

    superAstroInteractor.paySuperAstro(superastroListModel, lotteries, request.value, isRetry, transactionType, request.transactionTime, request.sequenceTransaction)
        ?.subscribeOn(Schedulers.io())
        ?.observeOn(AndroidSchedulers.mainThread())
        ?.subscribe ({

            if(it.isSuccess) {
                CURRENT_SUPERASTRO_BET = RepeatSuperAstroModel().apply {
                    this.superastroList = request.superastroList ?: arrayListOf()
                    this.lotteries = request.lotteries ?: arrayListOf()
                    this.value = request.value
                    this.stub = request.stubs
                }

                var drawDate = ""
                lotteries.forEachIndexed { index, lotteryModel ->
                    if (index < lotteries.count() - 1){
                        drawDate = drawDate + "${lotteryModel.date} ${lotteryModel.hour} - "
                    }else{
                        drawDate = drawDate + "${lotteryModel.date} ${lotteryModel.hour}"
                    }
                }

                var draw = ""
                lotteries.forEach{
                    if (DeviceUtil.isSalePoint()){
                        draw += "${it.fullName} "
                    }else{
                        draw += "${it.fullName} ${it.hour?.replace(":", "")}  "
                    }
                }

                val printModel = SuperAstroPrintModel().apply {
                    textHeader = ticketHeader
                    this.drawDate = drawDate
                    this.stub = request.stubs
                    this.lotteries = lotteries
                    this.checkNumber = it.checkNumber
                    this.digitChecking = it.digitChecking
                    this.superastroList = superastroListModel
                    this.ticketPrizePlan = this@executePaySuperAstro.ticketPrizePlan
                    this.totalValueIva = it.totalValueIva
                    this.totalValuePaid = it.totalValuePaid
                    this.totalValueBetting = it.totalValueBetting
                    this.draw = draw
                    this.ticketFooter = this@executePaySuperAstro.ticketFooter
                }

                StorageUtil.updateStub(it.serie1 ?: "", it.currentSerie2 ?: "")
                superAstroInteractor.print(printModel, transactionType != null){
                    if (it == PrinterStatus.OK){

                        BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", R_string(R.string.message_success_bet), true))
                    }else{
                        BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", R_string(R.string.message_error_print_device)))
                    }
                }
            } else {
                removeLastTransaction()
                BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", it.message ?: R_string(R.string.message_error_transaction)))
            }
        }, {
            removeLastTransaction(it)
            if (it is ConnectException){
                singleLiveEvent.value = SuperAstroViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.value = SuperAstroViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
            }
        })
}