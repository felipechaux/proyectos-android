package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.database.entities.ProductParameterEntityRoom
import co.com.pagatodo.core.data.dto.request.RequestSellSportingBetDTO
import co.com.pagatodo.core.data.dto.response.ResponseSellSportingBetDTO
import co.com.pagatodo.core.data.dto.response.ResponseSportingsDTO
import co.com.pagatodo.core.data.model.print.SportingPrintModel
import co.com.pagatodo.core.data.print.PrinterStatus
import io.reactivex.Observable

interface ISportingRepository {
    fun getParameters(): Observable<ResponseSportingsDTO>?
    fun sellSportingBet(requestSellSportingBetDTO: RequestSellSportingBetDTO, isRetry: Boolean = true, transactionType: String? = null): Observable<ResponseSellSportingBetDTO>?
    fun getProductParametersForSportingsRoom(): Observable<List<ProductParameterEntityRoom>>?
    fun print(sportinModel: SportingPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
}