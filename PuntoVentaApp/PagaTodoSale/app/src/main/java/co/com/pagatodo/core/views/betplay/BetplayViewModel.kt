package co.com.pagatodo.core.views.betplay

import android.annotation.SuppressLint
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.interactors.BetplayInteractor
import co.com.pagatodo.core.data.model.KeyValueModel
import co.com.pagatodo.core.data.model.ProductName
import co.com.pagatodo.core.data.model.request.RequestBetplayModel
import co.com.pagatodo.core.data.model.response.ResponseBetplayRechargeModel
import co.com.pagatodo.core.data.print.IPrinterStatusInfo
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.BetplayModule
import co.com.pagatodo.core.di.DaggerBetplayComponent
import co.com.pagatodo.core.di.PrinterStatusModule
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
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
class BetplayViewModel : BaseViewModel() {

    sealed class ViewEvent {
        class ResponseError(val errorMessage: String?) : ViewEvent()
        class ResponseSuccess(val successMessage: String?) : ViewEvent()
        class ResponseKeyValueParameters(val parameters: List<KeyValueModel>) : ViewEvent()
        class ResponseReprintValues(val document: String, val value: String) : ViewEvent()
    }

    @Inject
    lateinit var betplayInteractor: BetplayInteractor
    @Inject
    lateinit var printerStatusInfo: IPrinterStatusInfo

    lateinit var responseBetplay: ResponseBetplayRechargeModel

    var singleLiveEvent: SingleLiveEvent<ViewEvent> =
        SingleLiveEvent()

    init {
        DaggerBetplayComponent
            .builder()
            .betplayModule(BetplayModule())
            .printerStatusModule(PrinterStatusModule())
            .build().inject(this)
    }

    fun isValidDocument(
        request: RequestBetplayModel,
        isRetry: Boolean = true,
        transactionType: String? = null
    ) {
        documentConsult(request, isRetry, transactionType)
    }

    fun validateDocument(document: String) {
        queryDocument(document)
    }

    fun loadProductParameters() {
        getProductParameters()
    }

    fun dispatchBetplay() {
        executeBetplayReprint()
    }

    fun reprint() {
        dispatchBetplayReprint()
    }

    fun dispatchPinQuickBet(document: String) {
        executePinQuickBet(document)
    }

    fun dispatchCollectBetplay(document: String, value: String) {
        validatePrinterStatus(printerStatusInfo) {
            val totalValue = value.replace("$", "").replace(".", "")
            executeGeneratePin(document, totalValue)
        }
    }

    fun dispatchCheckOutBetplay(document: String, value: Int, pin: Int) {

        validatePrinterStatus(printerStatusInfo) {

            checkOutBetplay(document, value, pin)

        }

    }
}


private fun BetplayViewModel.documentConsult(
    request: RequestBetplayModel,
    isRetry: Boolean = true,
    transactionType: String? = null
) {
    validatePrinterStatus(printerStatusInfo) {
        executePayBetplay(request, isRetry, transactionType)
    }
}

private fun BetplayViewModel.dispatchBetplayReprint() {
    validatePrinterStatus(printerStatusInfo) {
        reprintTicket()
    }
}

@SuppressLint("CheckResult")
private fun BetplayViewModel.executePayBetplay(
    request: RequestBetplayModel,
    isRetry: Boolean = true,
    transactionType: String? = null
) {
    betplayInteractor.isDocumentValid(request.numberDoc)
        ?.subscribeOn(Schedulers.io())
        ?.observeOn(AndroidSchedulers.mainThread())
        ?.subscribe({
            if (it.success == true) {
                dispatchRecharge(request, isRetry, transactionType)
            } else {
                singleLiveEvent.value =
                    BetplayViewModel.ViewEvent.ResponseError(it.message)
            }
        }, {
            if (it is ConnectException) {
                singleLiveEvent.value =
                    BetplayViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.value =
                    BetplayViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
            }
        })
}

@SuppressLint("CheckResult")
private fun BetplayViewModel.queryDocument(document: String) {
    betplayInteractor.isDocumentValid(document)
        ?.subscribeOn(Schedulers.io())
        ?.observeOn(AndroidSchedulers.mainThread())
        ?.subscribe({
            if (it.success == true) {
                singleLiveEvent.value = BetplayViewModel.ViewEvent.ResponseSuccess(it.message)
            } else {
                singleLiveEvent.value = BetplayViewModel.ViewEvent.ResponseError(it.message)
            }
        }, {
            if (it is ConnectException) {
                singleLiveEvent.value =
                    BetplayViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.value =
                    BetplayViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
            }
        })
}

@SuppressLint("CheckResult")
private fun BetplayViewModel.dispatchRecharge(
    request: RequestBetplayModel,
    isRetry: Boolean = true,
    transactionType: String? = null
) {
    if (transactionType == null)
        saveLastTransaction(request, ProductName.BETPLAY.rawValue)

    betplayInteractor.payRecharge(
        request.numberDoc,
        request.value,
        isRetry,
        transactionType,
        request.transactionTime,
        request.sequenceTransaction
    )
        ?.subscribeOn(Schedulers.io())
        ?.observeOn(AndroidSchedulers.mainThread())
        ?.subscribe({

            if (it.isSuccess == true) {

                if (it.serie1 != null && it.currentSerie2 != null)
                    StorageUtil.updateStub(it.serie1 ?: "", it.currentSerie2 ?: "")

                betplayInteractor.print(
                    request.numberDoc,
                    request.value.toInt(),
                    request.stubs,
                    it.securityCode ?: "",
                    transactionType != null,
                    R_string(R.string.print_betplay_recharge),
                    false,
                    false
                ) {
                    if (it == PrinterStatus.OK
                    ) {

                        BaseObservableViewModel.baseSubject.onNext(
                            BaseEvents.ShowAlertDialogInMenu(
                                "",
                                R_string(R.string.message_success_recharge),
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
                removeLastTransaction()
                BaseObservableViewModel.baseSubject.onNext(
                    BaseEvents.ShowAlertDialogInMenu(
                        "",
                        R_string(R.string.message_error_recharge_failed)
                    )
                )
            }
        }, {
            removeLastTransaction(it)
            if (it is ConnectException) {
                singleLiveEvent.value =
                    BetplayViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.value =
                    BetplayViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
            }
        })
}

@SuppressLint("CheckResult")
private fun BetplayViewModel.executeBetplayReprint() {
    betplayInteractor.reprintBetplay()
        ?.subscribeOn(Schedulers.io())
        ?.observeOn(AndroidSchedulers.mainThread())
        ?.subscribe({
            BaseObservableViewModel.baseSubject.onNext(BaseEvents.HideProgressDialog)
            if (it.isSuccess == true) {

                if (it.serie1 != null && it.currentSerie2 != null)
                    StorageUtil.updateStub(it.serie1 ?: "", it.currentSerie2 ?: "")

                responseBetplay = it

                singleLiveEvent.value = BetplayViewModel.ViewEvent.ResponseReprintValues(it.documentNumber ?: "", it.value ?: "")
            } else {
                BaseObservableViewModel.baseSubject.onNext(
                    BaseEvents.ShowAlertDialogInMenu(
                        "",
                        R_string(R.string.message_error_recharge_failed)
                    )
                )
            }
        }, {
            if (it is ConnectException) {
                singleLiveEvent.value =
                    BetplayViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.value =
                    BetplayViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
            }
        })
}

@SuppressLint("CheckResult")
private fun BetplayViewModel.executePinQuickBet(document: String) {
    betplayInteractor.quickBetBetplay(document)
        ?.subscribeOn(Schedulers.io())
        ?.observeOn(AndroidSchedulers.mainThread())
        ?.subscribe({
            BaseObservableViewModel.baseSubject.onNext(BaseEvents.HideProgressDialog)
            if (it.isSuccess == true) {
                var title = R_string(R.string.text_success_transaction)
                var message = R_string(R.string.message_success_quick_bet)
                if (!it.status.equals("N")) {
                    title = ""
                    message = it.message ?: R_string(R.string.title_failed_transaction)
                    BaseObservableViewModel.baseSubject.onNext(
                        BaseEvents.ShowAlertDialogInMenu(
                            title,
                            message,
                            false
                        )
                    )
                } else {
                    BaseObservableViewModel.baseSubject.onNext(
                        BaseEvents.ShowAlertDialogInMenu(
                            title,
                            message,
                            true
                        )
                    )
                }

            } else {
                BaseObservableViewModel.baseSubject.onNext(
                    BaseEvents.ShowAlertDialogInMenu(
                        R_string(
                            R.string.message_error_document_invalid
                        ), R_string(R.string.message_error_user_not_valid)
                    )
                )
            }
        }, {
            if (it is ConnectException) {
                singleLiveEvent.value =
                    BetplayViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.value =
                    BetplayViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
            }
        })
}

@SuppressLint("CheckResult")
private fun BetplayViewModel.executeGeneratePin(document: String, value: String) {
    betplayInteractor.collectBetplay(document, value)
        ?.subscribeOn(Schedulers.io())
        ?.observeOn(AndroidSchedulers.mainThread())
        ?.subscribe({

            if (it.serie1 != null && it.currentSerie2 != null)
                StorageUtil.updateStub(it.serie1 ?: "", it.currentSerie2 ?: "")

            BaseObservableViewModel.baseSubject.onNext(BaseEvents.HideProgressDialog)
            if (it.isSuccess == true) {
                val serie1 = getPreference<String>(R_string(R.string.shared_key_current_serie1))
                val serie2 = getPreference<String>(R_string(R.string.shared_key_current_serie2))
                val stubs = "$serie1 $serie2"
                betplayInteractor.print(
                    document,
                    value.toInt(),
                    stubs,
                    it.securityCode ?: "",
                    false,
                    R_string(R.string.print_betplay_retirement),
                    false,
                    true
                ) { printStatus ->
                    if (printStatus == PrinterStatus.OK) {
                        BaseObservableViewModel.baseSubject.onNext(
                            BaseEvents.ShowAlertDialogInMenu(
                                R_string(R.string.text_success_transaction),
                                R_string(R.string.message_success_collect_betplay),
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
            if (it is ConnectException) {
                singleLiveEvent.value =
                    BetplayViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.value =
                    BetplayViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
            }
        })
}

@SuppressLint("CheckResult")
private fun BetplayViewModel.reprintTicket() {

    val serie1 = responseBetplay.serie1
    val serie2 = responseBetplay.serie2

    val stubs = "$serie1 $serie2"
    betplayInteractor.print(
        responseBetplay.documentNumber ?: "",
        (responseBetplay.value ?: "0").toInt(),
        stubs,
        responseBetplay.securityCode ?: "",
        false,
        R_string(R.string.print_betplay_recharge),
        true,
        false
    ) {
        if (it == PrinterStatus.OK
        ) {
            BaseObservableViewModel.baseSubject.onNext(
                BaseEvents.ShowAlertDialogInMenu(
                    R_string(R.string.title_success_reprint),
                    R_string(R.string.message_success_reprint),
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

@SuppressLint("CheckResult")
private fun BetplayViewModel.getProductParameters() {
    betplayInteractor.getProductParametersForBetplay()?.subscribe({
        singleLiveEvent.value = BetplayViewModel.ViewEvent.ResponseKeyValueParameters(it)
    }, {
        if (it is ConnectException) {
            singleLiveEvent.value =
                BetplayViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
        } else if (it !is SocketTimeoutException) {
            singleLiveEvent.value = BetplayViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
        }
    })
}

private fun BetplayViewModel.printCheckOutBetplay(numberDoc: String, stubs: String, value: String, securityCode: String) {

    betplayInteractor.print(
        numberDoc,
        value.toInt(),
        stubs,
        securityCode ?: "",
        false,
        R_string(R.string.print_betplay_collect),
        false,
        false
    ) {
        if (it == PrinterStatus.OK
        ) {
            BaseObservableViewModel.baseSubject.onNext(
                BaseEvents.ShowAlertDialogInMenu(
                    "",
                    R_string(R.string.message_success_collect_betplay_check_out),
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


@SuppressLint("CheckResult")
private fun BetplayViewModel.checkOutBetplay(document: String, value: Int, pin: Int) {

    betplayInteractor.checkOutBetplay(document, value, pin)?.subscribe({
        
        if (it.isSuccess ?: false) {

            if (it.serie1 != null && it.currentSerie2 != null)
                StorageUtil.updateStub(it.serie1 ?: "", it.currentSerie2 ?: "")

            printCheckOutBetplay(it.numDocumented?:"",
                "${it.serie1} ${it.serie2}",
                it.value?:"0",
                it.securityCode?:"")

        } else {
            singleLiveEvent.value =
                BetplayViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
        }

    }, {
        if (it is ConnectException) {
            singleLiveEvent.value =
                BetplayViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
        } else if (it !is SocketTimeoutException) {
            singleLiveEvent.value = BetplayViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction)   )
        }
    })

}