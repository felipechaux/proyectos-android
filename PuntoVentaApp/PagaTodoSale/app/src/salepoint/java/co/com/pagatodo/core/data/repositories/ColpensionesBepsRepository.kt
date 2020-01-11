package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.database.entities.ProductParameterEntityRoom
import co.com.pagatodo.core.data.dto.request.RequestColpensionesBepsMakeCollectionDTO
import co.com.pagatodo.core.data.dto.request.RequestColpensionesBepsQueryCollectionDTO
import co.com.pagatodo.core.data.dto.response.ResponseColpensionesBepsMakeCollectionDTO
import co.com.pagatodo.core.data.dto.response.ResponseColpensionesBepsQueryCollectionDTO
import co.com.pagatodo.core.data.model.print.ColpensionesBepsPrintModel
import co.com.pagatodo.core.data.print.ColpensionesBepsPrint
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.network.ApiFactory
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import io.reactivex.Observable

class ColpensionesBepsRepository: BaseRepository(), IColpensionesBepsRepository {
    override fun getProductParameters(): Observable<List<ProductParameterEntityRoom>>? {
        val productInfo = PagaTodoApplication.getDatabaseInstance()?.productDao()?.getProductInfo(R_string(R.string.code_product_colpensiones_beps))
        return Observable.just(productInfo?.productParameters)
    }

    override fun makeCollection(requestMakeCollection: RequestColpensionesBepsMakeCollectionDTO): Observable<ResponseColpensionesBepsMakeCollectionDTO>? {
        return ApiFactory.build()?.makeColpensionesCollection(requestMakeCollection)
    }

    override fun queryCollection(requestQueryCollection: RequestColpensionesBepsQueryCollectionDTO): Observable<ResponseColpensionesBepsQueryCollectionDTO>? {
        return ApiFactory.build()?.queryColpensionesCollection(requestQueryCollection)
    }

    override fun print(
        colpensionesBepsPrintModel: ColpensionesBepsPrintModel,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        ColpensionesBepsPrint().print(colpensionesBepsPrintModel, callback)
    }
}