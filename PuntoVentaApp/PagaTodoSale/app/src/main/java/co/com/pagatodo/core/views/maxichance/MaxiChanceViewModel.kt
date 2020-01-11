package co.com.pagatodo.core.views.maxichance

import android.annotation.SuppressLint
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.interactors.ILocalInteractor
import co.com.pagatodo.core.data.interactors.IUtilInteractor
import co.com.pagatodo.core.data.interactors.MaxiChanceInteractor
import co.com.pagatodo.core.data.model.*
import co.com.pagatodo.core.data.model.print.BasePrintModel
import co.com.pagatodo.core.data.model.request.RequestSuperChanceModel
import co.com.pagatodo.core.data.print.IPrinterStatusInfo
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.DaggerMaxiChanceComponent
import co.com.pagatodo.core.di.MaxiChanceModule
import co.com.pagatodo.core.di.PrinterStatusModule
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.DateUtil
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
class MaxiChanceViewModel: BaseViewModel() {

    sealed class ViewEvent {
        class ResponseError(val errorMessage: String?): ViewEvent()
        class ResponseSuccess(val successMessage: String?): ViewEvent()
        class ResponseRandomNumber(val numbers: List<String>): ViewEvent()
        class ResponseModalities(val promotional: PromotionModel): ViewEvent()
        class ResponseProductInfo(val productModel: ProductModel): ViewEvent()
    }

    @Inject
    lateinit var maxiChanceInteractor: MaxiChanceInteractor
    @Inject
    lateinit var utilInteractor: IUtilInteractor
    @Inject
    lateinit var localInteractor: ILocalInteractor
    @Inject
    lateinit var printerStatusInfo: IPrinterStatusInfo

    init {
        DaggerMaxiChanceComponent
            .builder()
            .maxiChanceModule(MaxiChanceModule())
            .printerStatusModule(PrinterStatusModule())
            .build().inject(this)
    }

    var singleLiveEvent: SingleLiveEvent<ViewEvent> =
        SingleLiveEvent()

    @SuppressLint("CheckResult")
    fun loadPromotional() {
        maxiChanceInteractor.getPromotionalForMaxiChance()?.subscribe ({
            if(it != null) {
                singleLiveEvent.postValue(ViewEvent.ResponseModalities(it))
            } else {
                singleLiveEvent.postValue(ViewEvent.ResponseError(R_string(R.string.message_error_parameters_maxichance)))
            }
        },{
            if (it is ConnectException){
                singleLiveEvent.postValue(ViewEvent.ResponseError(R_string(R.string.message_no_network)))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.postValue(ViewEvent.ResponseError( R_string(R.string.message_error_transaction)))
            }
        })
    }

    @SuppressLint("CheckResult")
    fun loadRandomNumbers(quantityNumber: Int, quantityDigits: Int) {
        utilInteractor.getRandomNumbers(quantityNumber, quantityDigits)?.subscribe ({
            singleLiveEvent.value =
                ViewEvent.ResponseRandomNumber(it)
        },{
            if (it is ConnectException){
                singleLiveEvent.value = ViewEvent.ResponseError(R_string(R.string.message_no_network))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.value = ViewEvent.ResponseError( R_string(R.string.message_error_transaction))
            }
        })
    }

    @SuppressLint("CheckResult")
    fun fetchChanceProduct() {
        localInteractor.getProductInfo(R_string(R.string.code_chance)).subscribe ({
            singleLiveEvent.value =
                ViewEvent.ResponseProductInfo(it)
        },{
            if (it is ConnectException){
                singleLiveEvent.value = ViewEvent.ResponseError(R_string(R.string.message_no_network))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.value = ViewEvent.ResponseError( R_string(R.string.message_error_transaction))
            }
        })
    }

    fun payMaxichance(request: RequestSuperChanceModel, isRetry: Boolean = true, transactionType: String? = null) {
        validatePrinterStatus(printerStatusInfo){
            executePayMaxichance(request, isRetry, transactionType)
        }
    }

    @SuppressLint("CheckResult")
    fun executePayMaxichance(request: RequestSuperChanceModel, isRetry: Boolean = true, transactionType: String? = null){
        if (transactionType == null)
            saveLastTransaction(request, ProductName.MAXI_CHANCE.rawValue)

        val superchanceList = request.chances ?: arrayListOf()
        val lotteries = request.lotteries ?: arrayListOf()

        maxiChanceInteractor.payMaxichance(superchanceList, lotteries, request.totalValue, isRetry, transactionType, request.transactionTime, request.sequenceTransaction)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe ({

                if (it.isSuccess) {

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

                    val listBest = mutableListOf<SuperchanceModel>()
                    it.answerBets?.forEach {
                        listBest.add(SuperchanceModel().apply {
                            number = it.numberBet.toString()
                            this.value = it.valueBet.toString()
                        })
                    }

                    val dateDraw = printModel.raffleDate
                    val dateGame = "${DateUtil.convertDateToStringFormat(DateUtil.StringFormat.DDMMYYYY_SPLIT_DASH, ( DateUtil.convertStringToDateFormat(DateUtil.StringFormat.DDMMYY,it.date!!)))}|${it.hour}|${dateDraw}"
                    printModel.apply {
                        this.raffleDate = dateGame
                    }

                    StorageUtil.updateStub(it.serie1 ?: "", it.currentSerie2 ?: "")
                    maxiChanceInteractor.print(printModel, listBest, transactionType != null){
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
                    singleLiveEvent.value = ViewEvent.ResponseError(R_string(R.string.message_no_network))
                } else if (it !is SocketTimeoutException) {
                    singleLiveEvent.value = ViewEvent.ResponseError( R_string(R.string.message_error_transaction))
                }
            })
    }
}