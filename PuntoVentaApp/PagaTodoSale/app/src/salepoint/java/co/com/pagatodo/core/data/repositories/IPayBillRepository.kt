package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.dto.request.RequestGetBillDTO
import co.com.pagatodo.core.data.dto.request.RequestPayBillDTO
import co.com.pagatodo.core.data.dto.request.RequestPayBillRePrintDTO
import co.com.pagatodo.core.data.dto.response.ResponseGetBillDTO
import co.com.pagatodo.core.data.dto.response.ResponsePayBillDTO
import co.com.pagatodo.core.data.dto.response.ResponsePayBillGetAgreementsDTO
import co.com.pagatodo.core.data.dto.response.ResponsePayBillRePrintDTO
import co.com.pagatodo.core.data.model.print.PayBillPrintModel
import co.com.pagatodo.core.data.print.PrinterStatus
import io.reactivex.Observable

interface IPayBillRepository {
    fun getAgreements(): Observable<ResponsePayBillGetAgreementsDTO>?
    fun getBill(requestGetBillDTO: RequestGetBillDTO): Observable<ResponseGetBillDTO>?
    fun payBill(requestPayBillDTO: RequestPayBillDTO): Observable<ResponsePayBillDTO>?
    fun print(printModel: PayBillPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
    fun rePrint(requestPayBillRePrintDTO: RequestPayBillRePrintDTO): Observable<ResponsePayBillRePrintDTO>?
}