package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.dto.request.RequestColsubsidioDTO
import co.com.pagatodo.core.data.dto.RequestColsubsidioGetInitialDataDTO
import co.com.pagatodo.core.data.dto.request.*
import co.com.pagatodo.core.data.dto.response.*
import co.com.pagatodo.core.network.ApiFactory
import io.reactivex.Observable
import javax.inject.Singleton

interface IColsubsidioCollectionRepository {
    fun queryColsubsidioGetInitialData(requestQuerySnrConceptsDTO: RequestColsubsidioGetInitialDataDTO
    ): Observable<ResponseColsubsidioGetInitialDataDTO>?
    fun queryColsubsidioGetPaymentMethods(requestQuerySnrGetOfficesDTO: RequestColsubsidioDTO
    ): Observable<ResponseColsubsidioGetPaymentMethodsDTO>?
    fun queryColsubsidioGetProducts(requestQuerySnrGetParametersDTO: RequestColsubsidioDTO
    ): Observable<ResponseColsubsidioGetProductsDTO>?
    fun queryColsubsidioGetBumperProducts(requestQuerySnrMakeCollectionsDTO: RequestColsubsidioDTO
    ): Observable<ResponseColsubsidioBumperProductsDTO>?
    fun queryColsubsidioCollectObligation(requestQuerySnrMakeCollectionsDTO: RequestColsubsidioCollectObligationDTO
    ): Observable<ResponseColsubsidioCollectObligationDTO>?
}

@Singleton
class ColsubsidioCollectionRepository: BaseRepository(),
    IColsubsidioCollectionRepository {

    override fun queryColsubsidioGetInitialData(requestColsubsidioGetInitialDataDTO: RequestColsubsidioGetInitialDataDTO
    ): Observable<ResponseColsubsidioGetInitialDataDTO>? {
        return ApiFactory.build()?.queryColsubsidioGetInitialData(requestColsubsidioGetInitialDataDTO)
    }

    override fun queryColsubsidioGetPaymentMethods(requestColsubsidioDTO: RequestColsubsidioDTO
    ): Observable<ResponseColsubsidioGetPaymentMethodsDTO>? {
        return ApiFactory.build()?.queryColsubsidioGetPaymentMethods(requestColsubsidioDTO)
    }

    override fun queryColsubsidioGetProducts(requestColsubsidioDTO: RequestColsubsidioDTO
    ): Observable<ResponseColsubsidioGetProductsDTO>? {
        return ApiFactory.build()?.queryColsubsidioGetProducts(requestColsubsidioDTO)
    }

    override fun queryColsubsidioGetBumperProducts(requestColsubsidioDTO: RequestColsubsidioDTO
    ): Observable<ResponseColsubsidioBumperProductsDTO>? {
        return ApiFactory.build()?.queryColsubsidioGetBumperProducts(requestColsubsidioDTO)
    }

    override fun queryColsubsidioCollectObligation(requestColsubsidioCollectObligationDTO: RequestColsubsidioCollectObligationDTO
    ): Observable<ResponseColsubsidioCollectObligationDTO>? {
        return ApiFactory.build()?.queryColsubsidioCollectObligation(requestColsubsidioCollectObligationDTO)
    }

}