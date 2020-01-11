package co.com.pagatodo.core.views.bbva

import android.annotation.SuppressLint
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.BillRequestDTO
import co.com.pagatodo.core.data.dto.CategoriesDTO
import co.com.pagatodo.core.data.dto.CitiesDTO
import co.com.pagatodo.core.data.dto.request.RequestBbvaDTO
import co.com.pagatodo.core.data.dto.response.ResponseBbvaBillPaymentDTO
import co.com.pagatodo.core.data.dto.response.ResponseBbvaValidateBillDTO
import co.com.pagatodo.core.data.interactors.IBbvaInteractor
import co.com.pagatodo.core.data.model.print.BbvaBillPayPrintModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.BbvaModule
import co.com.pagatodo.core.di.DaggerBbvaComponent
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.SingleLiveEvent
import co.com.pagatodo.core.views.base.BaseEvents
import co.com.pagatodo.core.views.base.BaseObservableViewModel
import co.com.pagatodo.core.views.base.BaseViewModel
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject

class BbvaViewModel : BaseViewModel() {

    @Inject
    lateinit var interactor: IBbvaInteractor

    init {

        DaggerBbvaComponent
            .builder()
            .bbvaModule(BbvaModule())
            .build()
            .inject(this)
    }

    var singleLiveEvent: SingleLiveEvent<ViewEvent> = SingleLiveEvent()

    sealed class ViewEvent {

        class Errors(val errorMessage: String) : BbvaViewModel.ViewEvent()
        class Success(val message: String) : BbvaViewModel.ViewEvent()
        class QueryCitiesSuccess(val cities: List<CitiesDTO>?) : ViewEvent()
        class QueryaParametersSuccess(
            val categories: List<CategoriesDTO>?,
            val agreements: List<String>?
        ) : ViewEvent()

        class ValidateBill(val billRequestDTO: ResponseBbvaValidateBillDTO) : ViewEvent()
        class PaymentBill(val message: ResponseBbvaBillPaymentDTO) : ViewEvent()

    }

    @SuppressLint("CheckResult")
    fun billPayment(request: BillRequestDTO) {

        interactor.billPayment(request)?.subscribe({

            if (it.isSuccess ?: false) {
//                singleLiveEvent.value = ViewEvent.Success(it.message ?: "")
                printTicket(it.transactionId)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(
                    it.message ?: RUtil.R_string(R.string.message_error_transaction)
                )
            }


        }, {
            showErros(it)
        })

    }

    private fun printTicket(transactionId: String?) {
        val printModel = BbvaBillPayPrintModel().apply {
            transactionDate = null
            transactionType = null
            value = null
            voucherNumber = null
            box = null
            userId = null
            this.transactionId = transactionId

        }

        interactor.print(printModel) { status ->
            if (status == PrinterStatus.OK) {
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
                        R_string(R.string.message_error_transaction),
                        false
                    )
                )
            }
        }
    }


    @SuppressLint("CheckResult")
    fun accountDeposit(requestBbvaDTO: RequestBbvaDTO) {

        interactor.accountDeposit(requestBbvaDTO)?.subscribe({

            if (it.isSuccess ?: false) {
                singleLiveEvent.value = ViewEvent.Success(it.message ?: "")
            } else {
                singleLiveEvent.value = ViewEvent.Errors(
                    it.message ?: RUtil.R_string(R.string.message_error_transaction)
                )
            }

        }, {
            showErros(it)
        })

    }

    @SuppressLint("CheckResult")
    fun paymentObligations(requestBbvaDTO: RequestBbvaDTO) {

        interactor.paymentObligations(requestBbvaDTO)?.subscribe({

            if (it.isSuccess ?: false) {
                singleLiveEvent.value = ViewEvent.Success(it.message ?: "")
            } else {
                singleLiveEvent.value = ViewEvent.Errors(
                    it.message ?: RUtil.R_string(R.string.message_error_transaction)
                )
            }

        }, {
            showErros(it)
        })

    }

    @SuppressLint("CheckResult")
    fun withdrawalAndBalance(requestBbvaDTO: RequestBbvaDTO) {

        interactor.withdrawalAndBalance(requestBbvaDTO)?.subscribe({

            if (it.isSuccess ?: false) {
                singleLiveEvent.value = ViewEvent.Success(it.message ?: "")
            } else {
                singleLiveEvent.value = ViewEvent.Errors(
                    it.message ?: RUtil.R_string(R.string.message_error_transaction)
                )
            }

        }, {
            showErros(it)
        })

    }

    @SuppressLint("CheckResult")
    fun reprintTransaction() {

        interactor.reprintTransaction()?.subscribe({

            if (it.isSuccess ?: false) {
                //TODO: SPINILLA ->
            } else {
                singleLiveEvent.value = ViewEvent.Errors(
                    it.message ?: RUtil.R_string(R.string.message_error_transaction)
                )
            }

        }, {
            showErros(it)
        })

    }

    @SuppressLint("CheckResult")
    fun closeBox() {

        interactor.closeBox()?.subscribe({

            if (it.isSuccess ?: false) {
                //TODO: SPINILLA ->
            } else {
                singleLiveEvent.value = ViewEvent.Errors(
                    it.message ?: RUtil.R_string(R.string.message_error_transaction)
                )
            }

        }, {
            showErros(it)
        })

    }


    @SuppressLint("CheckResult")
    fun getCities(city: String) {

        interactor.getCities(city)?.subscribe({

            if (it.isSuccess!!) {

                singleLiveEvent.value = ViewEvent.QueryCitiesSuccess(it.cities)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(it.message!!)
            }

        }, {

            showErros(it)
        })

    }

    private fun showErros(it: Throwable) {

        if (it is ConnectException) {
            singleLiveEvent.value = ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
        } else if (it !is SocketTimeoutException) {
            singleLiveEvent.value =
                ViewEvent.Errors( RUtil.R_string(R.string.message_error_transaction))
        } else {
            singleLiveEvent.value = ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
        }

    }

    @SuppressLint("CheckResult")
    fun getParameters() {

        interactor.getParameter()?.subscribe({

            if (it.isSuccess!!) {

                singleLiveEvent.value =
                    ViewEvent.QueryaParametersSuccess(it.categories, it.agreements)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(it.message!!)
            }

        }, {

            showErros(it)
        })

    }

    @SuppressLint("CheckResult")
    fun validateBill(request: BillRequestDTO) {

        interactor.validateBill(request)?.subscribe({

            if (it.isSuccess!!) {

                singleLiveEvent.value =
                    ViewEvent.ValidateBill(it)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(it.message!!)
            }

        }, {

            showErros(it)
        })

    }


}


