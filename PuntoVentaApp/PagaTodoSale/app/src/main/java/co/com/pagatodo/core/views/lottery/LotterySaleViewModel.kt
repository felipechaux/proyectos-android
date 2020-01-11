package co.com.pagatodo.core.views.lottery

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.interactors.VirtualLotteryInteractor
import co.com.pagatodo.core.data.model.ProductName
import co.com.pagatodo.core.data.model.VirtualLotteryModel
import co.com.pagatodo.core.data.model.print.LotteriesPrintModel
import co.com.pagatodo.core.data.model.request.RequestSaleOfLotteries
import co.com.pagatodo.core.data.model.response.LotteryNumberModel
import co.com.pagatodo.core.data.model.response.ResponseCheckNumberLotteryModel
import co.com.pagatodo.core.data.model.response.ResponsePayPhysicalLotteryModel
import co.com.pagatodo.core.data.print.IPrinterStatusInfo
import co.com.pagatodo.core.data.print.PrintManager
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.DaggerVirtualLotteryComponent
import co.com.pagatodo.core.di.PrinterStatusModule
import co.com.pagatodo.core.di.VirtualLotteryModule
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.DateUtil.Companion.convertStringToDate
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.SingleLiveEvent
import co.com.pagatodo.core.views.base.*
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LotterySaleViewModel : BaseViewModel() {

    sealed class ViewEvent {
        class ResponseError(val errorMessage: String?) : ViewEvent()
        class ResponseSuccess(val successMessage: String?) : ViewEvent()
        class ResponseLotteries(val virtuaLotteries: List<VirtualLotteryModel>) : ViewEvent()
        class ResponseNumberLotteryModel(val checkNumberLottery: ResponseCheckNumberLotteryModel) :
            ViewEvent()

        class ResponseNumberLotteryRandom(val numberRandom: ResponseCheckNumberLotteryModel) :
            ViewEvent()
    }

    @Inject
    lateinit var virtualLotteryInteractor: VirtualLotteryInteractor
    @Inject
    lateinit var printerStatusInfo: IPrinterStatusInfo

    var singleLiveEvent: SingleLiveEvent<ViewEvent> = SingleLiveEvent()

    init {
        DaggerVirtualLotteryComponent
            .builder()
            .virtualLotteryModule(VirtualLotteryModule())
            .printerStatusModule(PrinterStatusModule())
            .build().inject(this)
    }

    @SuppressLint("CheckResult")
    fun loadLotteries(productCode: String) {
        virtualLotteryInteractor.getVirtualLotteries(productCode)?.subscribe({
            singleLiveEvent.value = ViewEvent.ResponseLotteries(it)
        }, {
            singleLiveEvent.value = ViewEvent.ResponseError( R_string(R.string.message_error_transaction))
        })
    }

    fun checkNumberLottery(number: String, codLottery: String, isVirtual: Boolean) {
        if (isVirtual) {
            checkVirtualLotteryNumber(number, codLottery)
        } else {
            checkPhysicalLotteryNumber(number, codLottery)
        }
    }

    @SuppressLint("CheckResult")
    fun getRandomNumber(codLottery: String) {
        loadRandomNumber(codLottery)
    }

    @SuppressLint("CheckResult")
    fun payPhysicalLottery(
        request: RequestSaleOfLotteries,
        isRetry: Boolean = true,
        transactionType: String? = null
    ) {
        if (transactionType == null)
            saveLastTransaction(request, ProductName.PHYSICAL_LOTTERY.rawValue)

        val value = request.value ?: ""
        val number = request.number ?: ""
        val fractions = request.fractions ?: ""
        val serie = request.serie ?: ""
        val lotteryCode = request.lotteryCode ?: ""
        val draw = request.draw ?: ""

        //TODO: Chance parameters to request
        virtualLotteryInteractor.payPhysicalLottery(
            value,
            number,
            fractions,
            serie,
            lotteryCode,
            draw,
            isRetry,
            transactionType,
            request.transactionTime,
            request.sequenceTransaction
        )?.subscribe({
            removeLastTransaction()

            if (it.isSuccess) {
                BaseObservableViewModel.baseSubject.onNext(BaseEvents.HideProgressDialog)
                singleLiveEvent.value =
                    ViewEvent.ResponseSuccess(R_string(R.string.text_success_transaction))
            } else {
                singleLiveEvent.value =
                    ViewEvent.ResponseError(it.message?:R_string(R.string.message_error_pay))
            }
        }, {
            removeLastTransaction(it)
            if (it is ConnectException) {
                singleLiveEvent.value =
                    ViewEvent.ResponseError(R_string(R.string.message_no_network))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.value = ViewEvent.ResponseError( R_string(R.string.message_error_transaction))
            }
        })
    }

    @SuppressLint("CheckResult")
    fun payVirtualLottery(
        request: RequestSaleOfLotteries,
        isRetry: Boolean = true,
        transactionType: String? = null
    ) {
        validatePrinterStatus(printerStatusInfo) {
            executePayVirtualLottery(request, isRetry, transactionType)
        }
    }

    @SuppressLint("CheckResult")
    fun executePayVirtualLottery(
        request: RequestSaleOfLotteries,
        isRetry: Boolean = true,
        transactionType: String? = null
    ) {
        saveLastTransaction(request, ProductName.VIRTUAL_LOTTERY.rawValue)

        val value = request.value ?: ""
        val number = request.number ?: ""
        val fractions = request.fractions ?: ""
        val serie = request.serie ?: ""
        val lotteryCode = request.lotteryCode ?: ""
        val draw = request.draw ?: ""
        val fullname = request.fullname ?: ""
        val award = request.award
        val drawDate = request.drawDate ?: ""
        val drawHour = request.drawHour ?: ""

        virtualLotteryInteractor.payVirtualLottery(
            value,
            number,
            fractions,
            serie,
            lotteryCode,
            draw,
            isRetry,
            transactionType,
            request.transactionTime,
            request.sequenceTransaction
        )?.subscribe({
            removeLastTransaction()

            if (it.isSuccess) {
                StorageUtil.updateStub(it.serie1 ?: "", it.currentSerie2 ?: "")
                virtualLotteryInteractor.print(
                    createLotteryModel(
                        it,
                        fullname,
                        award,
                        draw,
                        number,
                        serie,
                        drawDate,
                        drawHour,
                        request.fractionsLottery ?: "",
                        value,
                        it.numbers ?: arrayListOf(),
                        request.fractionValue ?: ""
                    ), transactionType != null
                ) {
                    if (it == PrinterStatus.OK) {
                        BaseObservableViewModel.baseSubject.onNext(
                            BaseEvents.ShowAlertDialogInMenu(
                                "",
                                R_string(R.string.text_success_transaction),
                                true
                            )
                        )
                    } else {
                        BaseObservableViewModel.baseSubject.onNext(
                            BaseEvents.ShowAlertDialogInMenu(
                                "",
                                R_string(R.string.message_error_print_device)
                            )
                        )
                    }
                }
            } else {
                BaseObservableViewModel.baseSubject.onNext(
                    BaseEvents.ShowAlertDialogInMenu(
                        "",
                        it.message ?: R_string(R.string.message_error_transaction)
                    )
                )
            }
        }, {
            removeLastTransaction(it)
            if (it is ConnectException) {
                singleLiveEvent.value =
                    ViewEvent.ResponseError(R_string(R.string.message_no_network))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.value = ViewEvent.ResponseError( R_string(R.string.message_error_transaction))
            }
        })
    }

    @SuppressLint("CheckResult")
    fun printFractions(listFractions: ResponseCheckNumberLotteryModel, model: VirtualLotteryModel){
        val isRetry: Boolean = true
        virtualLotteryInteractor.printFractionsAvailables(listFractions, model, isRetry){
            if (it == PrinterStatus.OK) {
                BaseObservableViewModel.baseSubject.onNext(
                    BaseEvents.ShowAlertDialogInMenu(
                        "",
                        R_string(R.string.message_success_recharge_print_success),
                        true
                    )
                )
            } else {
                BaseObservableViewModel.baseSubject.onNext(
                    BaseEvents.ShowAlertDialogInMenu(
                        "",
                        R_string(R.string.message_error_print_device)
                    )
                )
            }

        }
    }

}

/*Extensions*/
@SuppressLint("CheckResult")
private fun LotterySaleViewModel.checkPhysicalLotteryNumber(number: String, codLottery: String) {
    virtualLotteryInteractor.checkPhysicalLotteryNumber(number, codLottery)?.subscribe({
        if (it.isSuccess) {
            singleLiveEvent.value =
                LotterySaleViewModel.ViewEvent.ResponseNumberLotteryModel(it)
        } else {
            singleLiveEvent.value =
                LotterySaleViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_check_number_lottery))
        }
    }, {
        if (it is ConnectException) {
            singleLiveEvent.value =
                LotterySaleViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
        } else if (it !is SocketTimeoutException) {
            singleLiveEvent.value = LotterySaleViewModel.ViewEvent.ResponseError( R_string(R.string.message_error_transaction))
        }
    })
}

@SuppressLint("CheckResult")
private fun LotterySaleViewModel.checkVirtualLotteryNumber(number: String, codLottery: String) {
    virtualLotteryInteractor.checkVirtualLotteryNumber(number, codLottery)?.subscribe({
        if (it.isSuccess) {
            singleLiveEvent.value =
                LotterySaleViewModel.ViewEvent.ResponseNumberLotteryModel(it)
        } else {
            singleLiveEvent.value =
                LotterySaleViewModel.ViewEvent.ResponseError(it.message)
        }
    }, {
        if (it is ConnectException) {
            singleLiveEvent.value =
                LotterySaleViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
        } else if (it !is SocketTimeoutException) {
            singleLiveEvent.value = LotterySaleViewModel.ViewEvent.ResponseError( R_string(R.string.message_error_transaction))
        }
    })
}

@SuppressLint("CheckResult")
fun LotterySaleViewModel.loadRandomNumber(codLottery: String) {
    virtualLotteryInteractor.loadRandomVirtualLottery(codLottery)?.subscribe({

        if(it.isSuccess){
            singleLiveEvent.value = LotterySaleViewModel.ViewEvent.ResponseNumberLotteryRandom(it)
        }else{
            singleLiveEvent.value = LotterySaleViewModel.ViewEvent.ResponseError(it.message)
        }


    }, {
        if (it is ConnectException) {
            singleLiveEvent.value =
                LotterySaleViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
        } else if (it !is SocketTimeoutException) {
            singleLiveEvent.value =
                LotterySaleViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
        }
    })
}

private fun createLotteryModel(
    model: ResponsePayPhysicalLotteryModel,
    fullname: String,
    awardPrice: Double?,
    draw: String,
    number: String,
    serie: String,
    drawDate: String,
    drawHour: String,
    fractions: String,
    value: String,
    numbers: List<LotteryNumberModel>,
    fractionValue: String
): LotteriesPrintModel {
    return LotteriesPrintModel().apply {
        lotteryName = fullname
        this.draw = draw
        this.drawDate = convertStringToDate(
            DateUtil.StringFormat.DDMMYYYY_SPLIT_DASH,
            DateUtil.addBackslashToStringDate(drawDate)
        )
        this.drawHour = drawHour
        digitChecked = model.checkDigit
        this.number = number
        this.numbers = numbers
        this.serie = serie
        prize = awardPrice?.let { formatValue(it) }
        fraction = fractions
        this.value = value
        this.fractionValue = fractionValue
    }
}