package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.request.RequestBalotoCheckTicketDTO
import co.com.pagatodo.core.data.dto.request.RequestBalotoParametersDTO
import co.com.pagatodo.core.data.dto.request.RequestBalotoSendBetDTO
import co.com.pagatodo.core.data.dto.request.RequestChargeTicketDTO
import co.com.pagatodo.core.data.dto.response.*
import co.com.pagatodo.core.data.model.BalotoParametersModel
import co.com.pagatodo.core.data.model.TransactionType
import co.com.pagatodo.core.data.model.print.BalotoPrintModel
import co.com.pagatodo.core.data.print.IBalotoPrint
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.print.PrintBalotoModule
import co.com.pagatodo.core.di.repository.DaggerBalotoRepositoryComponent
import co.com.pagatodo.core.network.ApiFactory
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil
import co.com.pagatodo.core.util.StorageUtil
import com.google.gson.Gson
import io.reactivex.Observable
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BalotoRepository : BaseRepository(), IBalotoRepository {

    @Inject
    lateinit var balotoPrint: IBalotoPrint

    init {
        DaggerBalotoRepositoryComponent
            .builder()
            .printBalotoModule(PrintBalotoModule())
            .build().inject(this)
    }

    override fun getBalotoResult(requestBalotoParametersDTO: RequestBalotoParametersDTO): Observable<ResponseBalotoResultsDTO>? {
        return ApiFactory.build()?.getBalotoResult(requestBalotoParametersDTO)
    }

    override fun getBalotoParameters(requestBalotoParametersDTO: RequestBalotoParametersDTO): Observable<ResponseBalotoParametersDTO>? {
        return ApiFactory.build(thirtySecondsTimeOut)?.getParametersBaloto(requestBalotoParametersDTO)
    }

    override fun sendBalotoBet(
        requestBalotoSendBetDTO: RequestBalotoSendBetDTO,
        isRetry: Boolean,
        transactionType: String?
    ): Observable<ResponseBalotoSendBetDTO>? {
        transactionType?.let {
            requestBalotoSendBetDTO.transactionType = it
        }

        if (!isRetry) {
            return ApiFactory.build()?.sendBalotoBet(requestBalotoSendBetDTO)
        }

        return ApiFactory.build()?.sendBalotoBet(requestBalotoSendBetDTO)
            ?.onErrorResumeNext { throwable: Throwable ->
                if (throwable is SocketTimeoutException) {
                    requestBalotoSendBetDTO.transactionType = TransactionType.RETRY.rawValue
                    ApiFactory
                        .build(StorageUtil.getTimeoutRetry())
                        ?.sendBalotoBet(requestBalotoSendBetDTO)
                        ?.retry(StorageUtil.getRetryCount())
                } else {
                    Observable.error(throwable)
                }
            }
    }

    override fun chargeTicket(requestChargeTicketDTO: RequestChargeTicketDTO): Observable<ResponseChargeTicketDTO>? {
        return ApiFactory.build(thirtySecondsTimeOut)?.chargeTicket(requestChargeTicketDTO)
    }

    override fun printBalotoSaleTicket(
        balotoPrintModel: BalotoPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        balotoPrint.printBalotoSaleTicket(balotoPrintModel, callback)
    }

    override fun checkStateTicket(requestBalotoCheckTicketDTO: RequestBalotoCheckTicketDTO): Observable<ResponseBalotoCheckTicketDTO>? {
        return ApiFactory.build(thirtySecondsTimeOut)?.checkStateTicket(requestBalotoCheckTicketDTO)
    }

    override fun printBalotoQueyTicket(
        transactionDate: String,
        numberTicket: String,
        valuePrize: String,
        tax: String,
        valuePay: String,
        terminalCode: String,
        salePointCode: String,
        authorizerNumber: String,
        stub: String,
        sellerCode: String,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        balotoPrint.printBalotoQueryTicket(
            transactionDate,
            numberTicket,
            valuePrize,
            tax,
            valuePay,
            terminalCode,
            salePointCode,
            authorizerNumber,
            stub,
            sellerCode,
            callback
        )

    }

    override fun saveLocalParameterBaloto(balotoParametersModel: BalotoParametersModel) {

        val jsonUser = Gson().toJson(balotoParametersModel)
        SharedPreferencesUtil.savePreference(
            RUtil.R_string(R.string.shared_key_baloto_parameter),
            jsonUser
        )

    }

    override fun getLocalParameterBaloto(): BalotoParametersModel {

        val json: String =
            SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_baloto_parameter))
        if (json.isNotEmpty())
            return Gson().fromJson(json, BalotoParametersModel::class.java).apply { isLoad = true };
        else
            return BalotoParametersModel().apply { isLoad = false }
    }

}