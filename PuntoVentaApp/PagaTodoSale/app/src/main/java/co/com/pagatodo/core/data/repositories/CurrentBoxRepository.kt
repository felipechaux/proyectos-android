package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.dto.request.RequestCurrentBoxDTO
import co.com.pagatodo.core.data.dto.response.ResponseCurrentBoxDTO
import co.com.pagatodo.core.data.model.ProductBoxModel
import co.com.pagatodo.core.data.print.IConsultsPrint
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.print.PrintConsultsModule
import co.com.pagatodo.core.di.repository.DaggerConsultsRepositoryComponent
import co.com.pagatodo.core.network.ApiFactory
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentBoxRepository: ICurrentBoxRepository {

    @Inject
    lateinit var consultsPrint: IConsultsPrint

    init {
        DaggerConsultsRepositoryComponent
            .builder()
            .printConsultsModule(PrintConsultsModule())
            .build().inject(this)
    }

    override fun checkCurrentBox(requestCurrentBoxDTO: RequestCurrentBoxDTO): Observable<ResponseCurrentBoxDTO>? {
        return ApiFactory.build()?.consultCurrentBox(requestCurrentBoxDTO)
    }

    override fun print(products: List<ProductBoxModel>, saleTotal: String, stubs: String, sellerName: String, callback: (printerStatus: PrinterStatus) -> Unit?) {
        consultsPrint.printConsult(products, saleTotal, stubs, sellerName, callback)
    }
}