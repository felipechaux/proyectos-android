package co.com.pagatodo.core.views.payu

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.interactors.IPayuCollectingInteractor
import co.com.pagatodo.core.data.model.LastTransactionStatusModel
import co.com.pagatodo.core.data.model.PayuModel
import co.com.pagatodo.core.data.model.print.PayuCollectingPrintModel
import co.com.pagatodo.core.data.model.response.PayuResponseReprintModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.DaggerPayuComponent
import co.com.pagatodo.core.di.PayuModule
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.views.SingleLiveEvent
import co.com.pagatodo.core.views.base.BaseEvents
import co.com.pagatodo.core.views.base.BaseObservableViewModel
import java.lang.ref.PhantomReference
import javax.inject.Inject

class PayuViewModel : ViewModel() {

    @Inject
    lateinit var interactor: IPayuCollectingInteractor

    sealed class ViewEvent {
        class ResponseError(val errorMessage: String?) : ViewEvent()
        class ResponseSuccess(val successMessage: String?) : ViewEvent()
        class QuerySuccess(val payuModel: PayuModel?) : ViewEvent()
    }

    var singleLiveEvent: SingleLiveEvent<ViewEvent> = SingleLiveEvent()
    lateinit var payu: MutableLiveData<PayuModel>
    lateinit var rePrintLastTransaction: PayuResponseReprintModel

    init {
        DaggerPayuComponent
            .builder().payuModule(PayuModule())
            .build().inject(this)

        if (!::payu.isInitialized) {
            payu = MutableLiveData()
        }
    }

    fun dispatchGetPayu(referenceNumber: String) {
        getPayu(referenceNumber)
    }


    fun dispatchGetPayu(payuModel: PayuModel, isReprint: Boolean = false) {
        collectPayu(payuModel)
    }

    fun dispatchQueryReprintPayu(payReference: String) {
        queryReprintPayu(payReference)
    }

    fun reprintPayu(payuModel: PayuModel) {
        payuReprint(payuModel)
    }


}

@SuppressLint("CheckResult")
private fun PayuViewModel.getPayu(referenceNumber: String) {
    interactor.getPayu(referenceNumber)?.subscribe({ it ->
        if (it.isSuccess == true) {

            val mPayu = PayuModel().apply {
                this.referenceNumber = it.payReference
                this.userName = it.payerName
                this.userDocument = it.payerDoc
                this.valueToPay = it.value
            }

            payu.apply {
                this.value?.referenceNumber = it.payReference
                this.value?.userName = it.payerName
                this.value?.userDocument = it.payerDoc
                this.value?.valueToPay = it.value
            }

            singleLiveEvent.value = PayuViewModel.ViewEvent.QuerySuccess(mPayu)


        } else {
            singleLiveEvent.value = PayuViewModel.ViewEvent.ResponseError(
                it.messages?.get(0).toString() ?: R_string(R.string.message_error_transaction)
            )
        }
    }, {
        singleLiveEvent.value =
            PayuViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
    })
}

@SuppressLint("CheckResult")
private fun PayuViewModel.queryReprintPayu(payReference: String) {
    interactor.reprint(payReference)?.subscribe({

        if (it.isSuccess == true) {

            val filterTransaction = it.transactions?.filter { it.reference == payReference}

            rePrintLastTransaction = it

            rePrintLastTransaction.apply {
                transactions = filterTransaction
            }

            if(rePrintLastTransaction.transactions?.count()?.compareTo(0) == 1){

                val lastTransaction = it.transactions?.last()

                val mPayu = PayuModel().apply {
                    this.referenceNumber = lastTransaction?.reference
                    this.userName = lastTransaction?.userNamePay
                    this.userDocument = lastTransaction?.documentUserPay
                    this.valueToPay = lastTransaction?.billValue.toString()
                }

                singleLiveEvent.value = PayuViewModel.ViewEvent.QuerySuccess(mPayu)

            }else{
                singleLiveEvent.value =
                    PayuViewModel.ViewEvent.ResponseError(R_string(R.string.payu_error_query_reprint))
            }


        } else {
            singleLiveEvent.value =
                PayuViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
        }

    }, {
        singleLiveEvent.value =
            PayuViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
    })


}

@SuppressLint("CheckResult")
private fun PayuViewModel.payuReprint(payuModel: PayuModel) {


    val lastTransaction = rePrintLastTransaction.transactions?.last()

    val printModel = PayuCollectingPrintModel().apply {
        textHeader = ""
        transactionNumber = lastTransaction?.prismaTransactionCode?:""
        voucherNumber = ""
        paymentReference = lastTransaction?.reference
        userDocument = payuModel.userDocument
        userNames = payuModel.userName
        message = lastTransaction?.responseMessage?:""
        collectedValue = payuModel.valueToPay
        transactionDate = lastTransaction?.effectiveDate
        textFooter = ""
        rentingCode = lastTransaction?.rentingCode
        terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
    }

    interactor.print(printModel, true) { status ->
        if (status == PrinterStatus.OK) {

            BaseObservableViewModel.baseSubject.onNext(
                BaseEvents.ShowAlertDialogInMenu(
                    "",
                    R_string(R.string.payu_reprint_success),
                    true
                )
            )

        } else {
            singleLiveEvent.value =
                PayuViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_print_device))
        }
    }


}

@SuppressLint("CheckResult")
private fun PayuViewModel.collectPayu(payuModel: PayuModel, isRetry: Boolean = false) {
    interactor.collectPayu(payuModel)?.subscribe({
        if (it.isSuccess == true) {
            val printModel = PayuCollectingPrintModel().apply {
                textHeader = ""
                transactionNumber = it.prismaTransactionCode
                voucherNumber = ""
                paymentReference = it.paymentReference
                userDocument = payuModel.userDocument
                userNames = payuModel.userName
                message = it.responseMessage
                collectedValue = payuModel.valueToPay
                transactionDate = it.transactionDate.plus(it.transactionHour)
                textFooter = ""
                rentingCode = it.retingCode
                terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            }

            interactor.print(printModel, isRetry) { status ->
                if (status == PrinterStatus.OK) {

                    BaseObservableViewModel.baseSubject.onNext(
                        BaseEvents.ShowAlertDialogInMenu(
                            R_string(R.string.giro_payment_success_title),
                            it.responseMessage!!,
                            true
                        )
                    )


                } else {
                    singleLiveEvent.value = PayuViewModel.ViewEvent.ResponseError(
                        it.responseMessage ?: R_string(R.string.message_error_print_device)
                    )
                }
            }
        } else {
            singleLiveEvent.value = PayuViewModel.ViewEvent.ResponseError(
                it.responseMessage ?: R_string(R.string.message_error_transaction)
            )
        }
    }, {
        singleLiveEvent.value =
            PayuViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
    })
}