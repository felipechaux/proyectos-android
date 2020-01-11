package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.dto.request.RequestGetPayuDTO
import co.com.pagatodo.core.data.dto.request.RequestPayuCollectingDTO
import co.com.pagatodo.core.data.dto.request.RequestPayuReprintDTO
import co.com.pagatodo.core.data.dto.response.ResponseGetPayuDTO
import co.com.pagatodo.core.data.dto.response.ResponsePayuCollectingDTO
import co.com.pagatodo.core.data.dto.response.ResponsePayuReprintDTO
import co.com.pagatodo.core.data.model.print.PayuCollectingPrintModel
import co.com.pagatodo.core.data.print.PayuPrint
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.network.ApiFactory
import io.reactivex.Observable

class PayuCollectingRepository: IPayuRepository {

    override fun getPayu(requestGetPayuDTO: RequestGetPayuDTO): Observable<ResponseGetPayuDTO>? {
        return ApiFactory.build()?.payuGetPayment(requestGetPayuDTO)
    }

    override fun collectPayu(requestPayuCollectingDTO: RequestPayuCollectingDTO): Observable<ResponsePayuCollectingDTO>? {
        return ApiFactory.build()?.collectPayu(requestPayuCollectingDTO)
    }

    override fun print(
        printModel: PayuCollectingPrintModel,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
       PayuPrint().print(printModel, isRetry, callback)
    }

    override fun reprint(requestPayuReprintDTO: RequestPayuReprintDTO): Observable<ResponsePayuReprintDTO>? {
        return ApiFactory.build()?.payuReprint(requestPayuReprintDTO)
    }
}