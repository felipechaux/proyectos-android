package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.database.entities.ProductParameterEntityRoom
import co.com.pagatodo.core.data.dto.request.*
import co.com.pagatodo.core.data.dto.response.*
import co.com.pagatodo.core.data.print.PrinterStatus
import io.reactivex.Observable

interface IBetplayRepository {
    fun reprintBetplay(requestDTO: RequestBetplayReprintDTO): Observable<ResponseBetplayRechargeDTO>?
    fun quickBet(requestDTO: RequestPinQuickBetDTO): Observable<ResponsePinQuickBetDTO>?
    fun collectBetplay(requestDTO: RequestGeneratePinDTO): Observable<ResponseGeneratePinDTO>?

    fun payRecharge(requestRechargeBetplayDTO: RequestRechargeBetplayDTO, isRetry: Boolean = true, transactionType: String? = null): Observable<ResponseBetplayRechargeDTO>?
    fun isDocumentValid(requestRechargeBetplayDTO: RequestRechargeBetplayDTO): Observable<ResponseDocumentConsultDTO>?
    fun getProductParametersForBetplayRoom(): Observable<List<ProductParameterEntityRoom>>?
    fun print(numberDoc: String, value: Int, stubs: String, digitChecking: String, isRetry: Boolean, typePrintText: String, isReprint: Boolean, isCollect: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
    fun checkOutBetplay( requestBetPlayCheckOut: RequestBetPlayCheckOut): Observable<ResponseBetPlayCheckOutDTO>?
}