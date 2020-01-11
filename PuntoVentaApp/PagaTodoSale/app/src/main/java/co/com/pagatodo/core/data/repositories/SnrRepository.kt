package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.dto.request.*
import co.com.pagatodo.core.data.dto.response.*
import co.com.pagatodo.core.network.ApiFactory
import io.reactivex.Observable
import javax.inject.Singleton

interface ISnrRepository {
    fun querySnrConcepts(requestQuerySnrConceptsDTO: RequestQuerySnrConceptsDTO): Observable<ResponseQuerySnrConceptsDTO>?
    fun querySnrOffices(requestQuerySnrGetOfficesDTO: RequestQuerySnrGetOfficesDTO): Observable<ResponseQuerySnrGetOfficesDTO>?
    fun querySnrGetParameters(requestQuerySnrGetParametersDTO: RequestQuerySnrGetParametersDTO): Observable<ResponseQuerySnrGetParametersDTO>?
    fun makeSnrCollections(requestQuerySnrMakeCollectionsDTO: RequestQuerySnrMakeCollectionsDTO): Observable<ResponseQuerySnrMakeCollectionsDTO>?
}

@Singleton
class SnrRepository: BaseRepository(), ISnrRepository{

    override fun querySnrConcepts(requestQuerySnrConceptsDTO: RequestQuerySnrConceptsDTO
    ): Observable<ResponseQuerySnrConceptsDTO>? {
        return ApiFactory.build()?.querySnrConcepts(requestQuerySnrConceptsDTO)
    }

    override fun querySnrOffices(requestQuerySnrGetOfficesDTO: RequestQuerySnrGetOfficesDTO
    ): Observable<ResponseQuerySnrGetOfficesDTO>? {
        return ApiFactory.build()?.querySnrOffices(requestQuerySnrGetOfficesDTO)
    }

    override fun querySnrGetParameters(requestQuerySnrGetParametersDTO: RequestQuerySnrGetParametersDTO
    ): Observable<ResponseQuerySnrGetParametersDTO>? {
        return ApiFactory.build()?.querySnrGetParameters(requestQuerySnrGetParametersDTO)
    }

    override fun makeSnrCollections(requestQuerySnrMakeCollectionsDTO: RequestQuerySnrMakeCollectionsDTO
    ): Observable<ResponseQuerySnrMakeCollectionsDTO>? {
        return ApiFactory.build()?.makeSnrCollections(requestQuerySnrMakeCollectionsDTO)
    }

}