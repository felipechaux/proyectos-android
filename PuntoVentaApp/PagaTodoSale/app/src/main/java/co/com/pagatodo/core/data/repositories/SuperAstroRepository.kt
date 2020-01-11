package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.database.entities.ModalityEntityRoom
import co.com.pagatodo.core.data.database.entities.ProductParameterEntityRoom
import co.com.pagatodo.core.data.dto.request.RequestSuperAstroDTO
import co.com.pagatodo.core.data.dto.response.ResponseChanceDTO
import co.com.pagatodo.core.data.model.TransactionType
import co.com.pagatodo.core.data.model.print.SuperAstroPrintModel
import co.com.pagatodo.core.data.print.ISuperAstroPrint
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.print.PrintSuperAstroModule
import co.com.pagatodo.core.di.repository.DaggerSuperAstroRepositoryComponent
import co.com.pagatodo.core.network.ApiFactory
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.StorageUtil
import io.reactivex.Observable
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SuperAstroRepository: BaseRepository(),
    ISuperAstroRepository {

    @Inject
    lateinit var superAstroPrint: ISuperAstroPrint

    init {
        DaggerSuperAstroRepositoryComponent
            .builder()
            .printSuperAstroModule(PrintSuperAstroModule())
            .build().inject(this)
    }

    override fun getProductParametersForSuperAstroRoom(): Observable<List<ProductParameterEntityRoom>>? {
        val productCode = getPreference<String>(R_string(R.string.code_product_superastro))
        val productInfo = PagaTodoApplication.getDatabaseInstance()?.productDao()?.getProductInfo(productCode)
        return Observable.just(productInfo?.productParameters)
    }

    override fun paySuperAstro(requestSuperAstroDTO: RequestSuperAstroDTO, isRetry: Boolean, transactionType: String?): Observable<ResponseChanceDTO>? {
        transactionType?.let {
            requestSuperAstroDTO.transactionType = it
        }

        if (!isRetry) {
            return ApiFactory.build()?.paySuperAstro(requestSuperAstroDTO)
        }

        return ApiFactory.build()?.paySuperAstro(requestSuperAstroDTO)
            ?.onErrorResumeNext { throwable: Throwable ->
                if (throwable is SocketTimeoutException) {
                    requestSuperAstroDTO.transactionType = TransactionType.RETRY.rawValue
                    ApiFactory
                        .build(StorageUtil.getTimeoutRetry())
                        ?.paySuperAstro(requestSuperAstroDTO)
                        ?.retry(StorageUtil.getRetryCount())
                } else {
                    Observable.error(throwable)
                }
            }
    }

    override fun getModalitiesRoom(): Observable<List<ModalityEntityRoom>>? {
        val productCode = getPreference<String>(R_string(R.string.code_product_superastro))
        PagaTodoApplication.getDatabaseInstance()
            ?.modalityDao()?.getAllByProductCode(productCode)?.let { motalities ->
                return Observable.create {
                    it.onNext(motalities)
                    it.onComplete()
                }
            } ?: run {
            return Observable.empty()
        }
    }

    override fun print(printModel: SuperAstroPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        printModel.sellerCode = getSellerCode()
        printModel.pdvCode = getPreference(R_string(R.string.shared_key_code_point_sale))
        superAstroPrint.print(printModel, isRetry, callback)

    }
}