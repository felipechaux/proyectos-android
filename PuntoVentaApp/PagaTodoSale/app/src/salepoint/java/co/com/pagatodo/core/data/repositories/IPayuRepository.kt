package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.dto.request.RequestGetPayuDTO
import co.com.pagatodo.core.data.dto.request.RequestPayuCollectingDTO
import co.com.pagatodo.core.data.dto.request.RequestPayuReprintDTO
import co.com.pagatodo.core.data.dto.response.ResponseGetPayuDTO
import co.com.pagatodo.core.data.dto.response.ResponsePayuCollectingDTO
import co.com.pagatodo.core.data.dto.response.ResponsePayuReprintDTO
import co.com.pagatodo.core.data.model.print.PayuCollectingPrintModel
import co.com.pagatodo.core.data.print.PrinterStatus
import io.reactivex.Observable

interface IPayuRepository {
    fun getPayu(requestGetBillDTO: RequestGetPayuDTO): Observable<ResponseGetPayuDTO>?
    fun collectPayu(requestPayuCollectingDTO: RequestPayuCollectingDTO): Observable<ResponsePayuCollectingDTO>?
    fun print(printModel: PayuCollectingPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
    fun reprint(requestPayuReprintDTO: RequestPayuReprintDTO): Observable<ResponsePayuReprintDTO>?
}