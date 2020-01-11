package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.dto.request.RequestCurrentBoxDTO
import co.com.pagatodo.core.data.dto.response.ResponseCurrentBoxDTO
import co.com.pagatodo.core.data.model.ProductBoxModel
import co.com.pagatodo.core.data.print.PrinterStatus
import io.reactivex.Observable

interface ICurrentBoxRepository {

    fun checkCurrentBox(requestCurrentBoxDTO: RequestCurrentBoxDTO): Observable<ResponseCurrentBoxDTO>?
    fun print(products: List<ProductBoxModel>, saleTotal: String, stubs: String, sellerName: String, callback: (printerStatus: PrinterStatus) -> Unit?)
}