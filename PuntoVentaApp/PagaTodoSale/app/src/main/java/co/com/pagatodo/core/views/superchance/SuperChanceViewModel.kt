package co.com.pagatodo.core.views.superchance

import android.annotation.SuppressLint
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.interactors.IUtilInteractor
import co.com.pagatodo.core.data.interactors.SuperChanceInteractor
import co.com.pagatodo.core.data.model.KeyValueModel
import co.com.pagatodo.core.data.model.ModalityModel
import co.com.pagatodo.core.data.model.ProductName
import co.com.pagatodo.core.data.model.SuperchanceModel
import co.com.pagatodo.core.data.model.print.BasePrintModel
import co.com.pagatodo.core.data.model.request.RequestSuperChanceModel
import co.com.pagatodo.core.data.print.IPrinterStatusInfo
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.DaggerSuperChanceComponent
import co.com.pagatodo.core.di.PrinterStatusModule
import co.com.pagatodo.core.di.SuperChanceModule
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
class SuperChanceViewModel: BaseViewModel() {

    sealed class ViewEvent {
        class ResponseError(val errorMessage: String?): ViewEvent()
        class ResponseSuccess(val successMessage: String?): ViewEvent()
        class ResponseModalities(val modalities: List<ModalityModel>): ViewEvent()
        class ResponseRandomNumber(val numbers: List<String>): ViewEvent()
        class ResponseKeyValueParameters(val parameters: List<KeyValueModel>): ViewEvent()
    }

    @Inject
    lateinit var superChanceInteractor: SuperChanceInteractor
    @Inject
    lateinit var utilInteractor: IUtilInteractor
    @Inject
    lateinit var printerStatusInfo: IPrinterStatusInfo

    init {
        DaggerSuperChanceComponent
            .builder()
            .superChanceModule(SuperChanceModule())
            .printerStatusModule(PrinterStatusModule())
            .build().inject(this)
    }

    var singleLiveEvent: SingleLiveEvent<ViewEvent> =
        SingleLiveEvent()

    fun loadProductParameters() {
        getProductParameters()
    }

    fun loadRandomNumbers(quantityNumber: Int, quantityDigits: Int) {
        getRandomNumbers(quantityNumber, quantityDigits)
    }
}

/*Extensions*/
@SuppressLint("CheckResult")
private fun SuperChanceViewModel.getProductParameters() {
    superChanceInteractor.getProductParametersForSuperChance()?.subscribe ({
        singleLiveEvent.value =
            SuperChanceViewModel.ViewEvent.ResponseKeyValueParameters(it)
    },{
        if (it is ConnectException){
            singleLiveEvent.value = SuperChanceViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
        } else if (it !is SocketTimeoutException) {
            singleLiveEvent.value =
                    SuperChanceViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
        }
    })
}

@SuppressLint("CheckResult")
private fun SuperChanceViewModel.getRandomNumbers(quantityNumber: Int, quantityDigits: Int) {
    utilInteractor.getRandomNumbers(quantityNumber, quantityDigits)?.subscribe ({
        singleLiveEvent.value =
            SuperChanceViewModel.ViewEvent.ResponseRandomNumber(it)
    },{
        if (it is ConnectException){
            singleLiveEvent.value = SuperChanceViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
        } else if (it !is SocketTimeoutException) {
            singleLiveEvent.value =
                    SuperChanceViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
        }
    })
}

fun SuperChanceViewModel.paySuperchance(request: RequestSuperChanceModel, isRetry: Boolean = true, transactionType: String? = null) {
    validatePrinterStatus(printerStatusInfo){
        executePaySuperChance(request, isRetry, transactionType)
    }
}

@SuppressLint("CheckResult")
private fun SuperChanceViewModel.executePaySuperChance(request: RequestSuperChanceModel, isRetry: Boolean = true, transactionType: String? = null){

    if (transactionType == null)
        saveLastTransaction(request, ProductName.SUPER_CHANCE.rawValue)

    val superchanceList = request.chances ?: arrayListOf()
    val lotteries = request.lotteries ?: arrayListOf()
    superChanceInteractor.paySuperchance(superchanceList, lotteries, request.totalValue, isRetry, transactionType, request.transactionTime, request.sequenceTransaction)
        ?.subscribeOn(Schedulers.io())
        ?.observeOn(AndroidSchedulers.mainThread())
        ?.subscribe ({


            if(it.isSuccess) {

                val printModel = BasePrintModel()
                it.uniqueSerial?.let{ printModel.uniqueSerial = it }
                it.digitChecking?.let{ printModel.digitChecking = it }
                it.totalValueBetting?.let{ printModel.totalValueBetting = it }
                it.totalValuePaid?.let { printModel.totalValuePaid = it }
                it.totalValueOverloaded?.let { printModel.totalValueOverloaded = it }
                it.totalValueIva?.let { printModel.totalValueIva = it }
                printModel.stubs = request.stubs
                request.raffleDateModel?.let {
                    printModel.raffleDate = DateUtil.convertDateToStringFormat(DateUtil.StringFormat.DDMMYYYY_SPLIT_DASH, it)
                }
                printModel.lotteries = lotteries

                val betsList = mutableListOf<SuperchanceModel>()
                it.answerBets?.forEach {
                    betsList.add(SuperchanceModel().apply {
                        number = it.numberBet.toString()
                        this.value = it.valueBet.toString()
                    })
                }

                StorageUtil.updateStub(it.serie1 ?: "", it.currentSerie2 ?: "")
                val dateDraw = printModel.raffleDate

                val dateGame = "${DateUtil.convertDateToStringFormat(DateUtil.StringFormat.DDMMYYYY_SPLIT_DASH, ( DateUtil.convertStringToDateFormat(DateUtil.StringFormat.DDMMYY,it.date!!)))}|${it.hour}|${dateDraw}"
                printModel.apply {
                    this.raffleDate = dateGame
                }

                printModel.apply { maxRows = request.maxRows }
                superChanceInteractor.print(printModel, betsList, transactionType != null){
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
        },{
            removeLastTransaction(it)
            if (it is ConnectException){
                singleLiveEvent.value = SuperChanceViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.value = SuperChanceViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
            }
        })
}