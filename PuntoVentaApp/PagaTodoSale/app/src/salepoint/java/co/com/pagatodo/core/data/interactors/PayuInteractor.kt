package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.request.RequestGetPayuDTO
import co.com.pagatodo.core.data.dto.request.RequestPayuCollectingDTO
import co.com.pagatodo.core.data.dto.request.RequestPayuReprintDTO
import co.com.pagatodo.core.data.model.PayuModel
import co.com.pagatodo.core.data.model.print.PayuCollectingPrintModel
import co.com.pagatodo.core.data.model.response.PayuResponseReprintModel
import co.com.pagatodo.core.data.model.response.PayuCollectingResponseModel
import co.com.pagatodo.core.data.model.response.PayuResponseModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.repositories.IPayuRepository
import co.com.pagatodo.core.di.DaggerPayuComponent
import co.com.pagatodo.core.di.PayuModule
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

interface IPayuCollectingInteractor {
    fun getPayu(referenceNumber: String): Observable<PayuResponseModel>?
    fun collectPayu(payuModel: PayuModel): Observable<PayuCollectingResponseModel>?
    fun print(printModel: PayuCollectingPrintModel, isReprint: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
    fun reprint(payReference:String): Observable<PayuResponseReprintModel>?
}

class PayuCollectingInteractor: IPayuCollectingInteractor {

    @Inject
    lateinit var repository: IPayuRepository

    init {
        DaggerPayuComponent.builder()
            .payuModule(PayuModule())
            .build().inject(this)
    }

    override fun getPayu(referenceNumber: String): Observable<PayuResponseModel>? {
        val request = RequestGetPayuDTO().apply {
            paymentReference = referenceNumber
            userType = getPreference(R_string(R.string.shared_key_user_type))
            macAddress = getPreference(R_string(R.string.shared_key_terminal_code))
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            productCode = R_string(R.string.code_product_pay_u_collecting)
        }

        return repository.getPayu(request)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = PayuResponseModel().apply{
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionHour
                    messages = it.messages
                    count = it.count
                    payReference = it.payReference
                    payerName = it.payerName
                    payerDoc = it.payerDoc
                    value = it.value
                    responseMessage = it.responseMessage
                    transactionQuantity = it.transactionQuantity
                }
                Observable.just(response)
            }
    }

    override fun collectPayu(payuModel: PayuModel): Observable<PayuCollectingResponseModel>? {
        val request = RequestPayuCollectingDTO().apply {
            count = false // TODO: Replace with real value
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            paymentReference = ""
            billValue = ""
            macAddress = getPreference(R_string(R.string.shared_key_terminal_code))
            deviceVolume = getPreference(R_string(R.string.shared_key_terminal_code))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            productCode = R_string(R.string.code_product_pay_u_collecting)
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            userType = getPreference(R_string(R.string.shared_key_user_type))
        }
        return repository.collectPayu(request)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = PayuCollectingResponseModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionHour
                    successful = it.successful
                    messages = it.messages
                    count = it.count
                    synNromov = it.synNromov
                    retingCode = it.retingCode
                    paymentReference = it.paymentReference
                    billValue = it.billValue
                    responseMessage = it.responseMessage
                    effectiveDate = it.effectiveDate
                    prismaTransactionCode = it.prismaTransactionCode
                    saleSite = it.saleSite
                    transactionQuantity = it.transactionQuantity
                    prismaTransactionCode = it.prismaTransactionCode
                    responseMessage = it.responseMessage
                }
                Observable.just(response)
            }
    }

    override fun print(
        printModel: PayuCollectingPrintModel,
        isReprint: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        repository.print(printModel, isReprint, callback)
    }



    override fun reprint(payReference:String): Observable<PayuResponseReprintModel>? {

        val request = RequestPayuReprintDTO().apply {
            userType = getPreference(R_string(R.string.shared_key_user_type))
            mac = getPreference(R_string(R.string.shared_key_terminal_code))
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            productCode = R_string(R.string.code_product_pay_u_collecting)
            this.payReference = payReference
        }

        return repository.reprint(request)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = PayuResponseReprintModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionHour
                    messages = it.messages
                    count = it.count
                    transactionQuantity = it.transactionQuantity
                    pageFoot = it.pageFoot
                    transactions = it.transactions
                }
                Observable.just(response)
            }
    }
}