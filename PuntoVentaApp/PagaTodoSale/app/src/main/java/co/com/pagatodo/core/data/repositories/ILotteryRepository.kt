package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.database.DBHelperResponse
import co.com.pagatodo.core.data.database.entities.LotteryEntityRoom
import co.com.pagatodo.core.data.database.entities.ProductLotteriesEntityRoom
import io.reactivex.Observable

interface ILotteryRepository {
    fun getAllLotteries(): List<LotteryEntityRoom>?
    fun getLotteriesByCodeRoom(code: String): Observable<List<LotteryEntityRoom>>?
    fun getLotteriesForChanceRoom(): Observable<List<LotteryEntityRoom>>?
    fun getLotteriesForSuperChanceRoom(): Observable<List<LotteryEntityRoom>>?
    fun getLotteriesForPayMillionaireRoom(): Observable<List<LotteryEntityRoom>>?
    fun getLotteriesForSuperAstroRoom(): Observable<List<LotteryEntityRoom>>?
    fun saveLotteriesInLocalRoom(lotteries: List<LotteryEntityRoom>): Observable<DBHelperResponse>
    fun deleteAllProductLotteries(): Observable<DBHelperResponse>
    fun saveProductLotteriesInLocal(productLotteries: List<ProductLotteriesEntityRoom>): Observable<DBHelperResponse>
}