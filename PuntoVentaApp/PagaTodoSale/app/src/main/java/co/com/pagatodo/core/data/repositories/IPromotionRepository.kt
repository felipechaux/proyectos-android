package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.database.DBHelperResponse
import co.com.pagatodo.core.data.database.entities.PromotionalEntityRoom
import io.reactivex.Observable

interface IPromotionRepository {
    fun savePromotionalInLocalRoom(promotional: List<PromotionalEntityRoom>): Observable<DBHelperResponse>
}