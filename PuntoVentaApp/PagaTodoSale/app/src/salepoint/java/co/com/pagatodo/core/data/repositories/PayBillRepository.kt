package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.request.RequestGetBillDTO
import co.com.pagatodo.core.data.dto.request.RequestPayBillDTO
import co.com.pagatodo.core.data.dto.request.RequestPayBillGetAgreementsDTO
import co.com.pagatodo.core.data.dto.request.RequestPayBillRePrintDTO
import co.com.pagatodo.core.data.dto.response.ResponseGetBillDTO
import co.com.pagatodo.core.data.dto.response.ResponsePayBillDTO
import co.com.pagatodo.core.data.dto.response.ResponsePayBillGetAgreementsDTO
import co.com.pagatodo.core.data.dto.response.ResponsePayBillRePrintDTO
import co.com.pagatodo.core.data.model.print.PayBillPrintModel
import co.com.pagatodo.core.data.print.PayBillPrint
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.network.ApiFactory
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.StorageUtil
import io.reactivex.Observable

class PayBillRepository: IPayBillRepository {
    override fun getAgreements(): Observable<ResponsePayBillGetAgreementsDTO>? {
        val requestPayBillGetAgreementsDTO = RequestPayBillGetAgreementsDTO().apply {
            userType = getPreference(R_string(R.string.shared_key_user_type))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            login = R_string(R.string.login_parameter)
            transactionTime = DateUtil.getCurrentDateInUnix()
            productCode = "860"
        }
        return ApiFactory.build()?.payBillGetAgreement(requestPayBillGetAgreementsDTO)
    }

    override fun getBill(requestGetBillDTO: RequestGetBillDTO): Observable<ResponseGetBillDTO>? {
        return ApiFactory.build()?.payBillGetBill(requestGetBillDTO)
    }

    override fun payBill(requestPayBillDTO: RequestPayBillDTO): Observable<ResponsePayBillDTO>? {
        return ApiFactory.build()?.payBill(requestPayBillDTO)
    }

    override fun rePrint(requestPayBillRePrintDTO: RequestPayBillRePrintDTO): Observable<ResponsePayBillRePrintDTO>? {
        return ApiFactory.build()?.payBillRePrint(requestPayBillRePrintDTO)
    }

    override fun print(
        printModel: PayBillPrintModel,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        PayBillPrint().print(printModel, isRetry, callback)
    }
}