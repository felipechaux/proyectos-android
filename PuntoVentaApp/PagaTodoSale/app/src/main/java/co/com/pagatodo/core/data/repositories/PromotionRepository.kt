package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.data.database.DBHelperOperations
import co.com.pagatodo.core.data.database.DBHelperResponse
import co.com.pagatodo.core.data.database.entities.PromotionalEntityRoom
import co.com.pagatodo.core.di.DaggerPromotionComponent
import co.com.pagatodo.core.di.PromotionModule
import io.reactivex.Observable
import java.lang.Exception
import javax.inject.Singleton

@Singleton
class PromotionRepository: IPromotionRepository {

    init {
        DaggerPromotionComponent.builder().promotionModule(PromotionModule()).build().inject(this)
    }

    override fun savePromotionalInLocalRoom(promotional: List<PromotionalEntityRoom>): Observable<DBHelperResponse> {
        val response = DBHelperResponse().apply {
            status = true
            operation = DBHelperOperations.insertRows
        }

        val dbInstance = PagaTodoApplication.getDatabaseInstance()
        try {
            dbInstance?.promotionalDao()?.insertPromotionals(promotional)
        }
        catch (e: Exception) {
            response.status = false
        }
        return Observable.just(response)
    }
}