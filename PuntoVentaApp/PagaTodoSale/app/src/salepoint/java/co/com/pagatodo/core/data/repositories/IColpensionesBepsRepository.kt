package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.database.entities.ProductParameterEntityRoom
import co.com.pagatodo.core.data.dto.request.RequestColpensionesBepsMakeCollectionDTO
import co.com.pagatodo.core.data.dto.request.RequestColpensionesBepsQueryCollectionDTO
import co.com.pagatodo.core.data.dto.response.ResponseColpensionesBepsMakeCollectionDTO
import co.com.pagatodo.core.data.dto.response.ResponseColpensionesBepsQueryCollectionDTO
import co.com.pagatodo.core.data.model.print.ColpensionesBepsPrintModel
import co.com.pagatodo.core.data.print.PrinterStatus
import io.reactivex.Observable

interface IColpensionesBepsRepository {
    fun getProductParameters(): Observable<List<ProductParameterEntityRoom>>?
    fun makeCollection(requestMakeCollection: RequestColpensionesBepsMakeCollectionDTO): Observable<ResponseColpensionesBepsMakeCollectionDTO>?
    fun queryCollection(requestQueryCollection: RequestColpensionesBepsQueryCollectionDTO): Observable<ResponseColpensionesBepsQueryCollectionDTO>?
    fun print(colpensionesBepsPrintModel: ColpensionesBepsPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?)
}