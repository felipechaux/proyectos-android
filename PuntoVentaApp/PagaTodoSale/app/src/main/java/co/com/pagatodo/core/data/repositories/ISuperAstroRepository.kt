package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.database.entities.ModalityEntityRoom
import co.com.pagatodo.core.data.database.entities.ProductParameterEntityRoom
import co.com.pagatodo.core.data.dto.request.RequestSuperAstroDTO
import co.com.pagatodo.core.data.dto.response.ResponseChanceDTO
import co.com.pagatodo.core.data.model.print.SuperAstroPrintModel
import co.com.pagatodo.core.data.print.PrinterStatus
import io.reactivex.Observable

interface ISuperAstroRepository {
    fun paySuperAstro(requestSuperAstroDTO: RequestSuperAstroDTO, isRetry: Boolean = true, transactionType: String? = null): Observable<ResponseChanceDTO>?
    fun getProductParametersForSuperAstroRoom(): Observable<List<ProductParameterEntityRoom>>?
    fun getModalitiesRoom(): Observable<List<ModalityEntityRoom>>?
    fun print(printModel: SuperAstroPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
}