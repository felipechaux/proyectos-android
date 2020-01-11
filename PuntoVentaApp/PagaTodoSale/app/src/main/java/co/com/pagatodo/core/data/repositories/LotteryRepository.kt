package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.database.DBHelperOperations
import co.com.pagatodo.core.data.database.DBHelperResponse
import co.com.pagatodo.core.data.database.entities.LotteryEntityRoom
import co.com.pagatodo.core.data.database.entities.ProductLotteriesEntityRoom
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import io.reactivex.Observable
import java.lang.Exception
import javax.inject.Singleton

@Singleton
class LotteryRepository: ILotteryRepository {

    //@Inject lateinit var dbHelper: DBHelper

    init {
        //DaggerLotteryComponent.builder().lotteryModule(LotteryModule()).build().inject(this)
    }

    override fun saveLotteriesInLocalRoom(lotteries: List<LotteryEntityRoom>): Observable<DBHelperResponse> {
        val response = DBHelperResponse().apply {
            status = true
            totalRows = 0
            identifier = null
            operation = DBHelperOperations.insertRows
        }

        val dbInstance = PagaTodoApplication.getDatabaseInstance()
        try {
            dbInstance?.lotteryDao()?.deleteAll()
            dbInstance?.lotteryDao()?.insertAll(lotteries)
        }
        catch (e: Exception) {
            response.status = false
        }
        return Observable.just(response)
    }

    override fun deleteAllProductLotteries(): Observable<DBHelperResponse> {
        val response = DBHelperResponse().apply {
            status = true
            operation = DBHelperOperations.deleteAllTable
        }
        try {
            PagaTodoApplication?.getDatabaseInstance()?.productLotteriesDao()?.deleteAll()
        }
        catch (e: Exception) {
            response.status = false
        }
        return Observable.just(response)
    }

    override fun saveProductLotteriesInLocal(productLotteries: List<ProductLotteriesEntityRoom>): Observable<DBHelperResponse> {
        val response = DBHelperResponse().apply {
            status = true
            totalRows = 0
            identifier = null
            operation = DBHelperOperations.insertRows
        }

        val dbInstance = PagaTodoApplication.getDatabaseInstance()
        try {
            dbInstance?.productLotteriesDao()?.insertAll(productLotteries)
        }
        catch (e: Exception) {
            response.status = false
        }
        return Observable.just(response)
    }

    override fun getLotteriesForChanceRoom(): Observable<List<LotteryEntityRoom>>? {
        return getLotteriesByCodeRoom(R_string(R.string.code_maxichance))
    }

    override fun getLotteriesForSuperChanceRoom(): Observable<List<LotteryEntityRoom>>? {
        return getLotteriesByCodeRoom(R_string(R.string.code_superchance))
    }
    override fun getLotteriesForPayMillionaireRoom(): Observable<List<LotteryEntityRoom>>? {
        return getLotteriesByCodeRoom(R_string(R.string.code_product_paymillionaire))
    }

    override fun getLotteriesForSuperAstroRoom(): Observable<List<LotteryEntityRoom>>? {
        return getLotteriesByCodeRoom(R_string(R.string.code_superastro))
    }

    override fun getAllLotteries(): List<LotteryEntityRoom>? {
        val dbInstance = PagaTodoApplication.getDatabaseInstance()
        var data = listOf<LotteryEntityRoom>()
        dbInstance?.lotteryDao()?.getAll().let {
            it?.let { listEntities ->
                data = listEntities
            }
        }
        return data
    }

    override fun getLotteriesByCodeRoom(code: String): Observable<List<LotteryEntityRoom>>? {
        val ids = arrayListOf<String>()
        val dbInstance = PagaTodoApplication.getDatabaseInstance()

        val productLotteriesEntity = dbInstance?.productLotteriesDao()?.getAll()
        productLotteriesEntity?.filter { it.productCode == code }
            ?.forEach { lottery ->
                lottery.lotteryCode.let { code ->
                    ids.add(code)
                }
            }

        var data = listOf<LotteryEntityRoom>()
        dbInstance?.lotteryDao()?.getAllByIds(ids).let {
            it?.let { listEntities ->
                data = listEntities
            }
        }

        val filteredData = filterLotteriesByHourRoom(data)
        filteredData?.let { filtered ->
            return Observable.create {
                it.onNext(filtered)
                it.onComplete()
            }
        } ?: run {
            return Observable.empty()
        }
    }

    private fun filterLotteriesByHourRoom(lotteries: List<LotteryEntityRoom>): List<LotteryEntityRoom>? {
        val currentHour = DateUtil.convertDateToStringFormat(DateUtil.StringFormat.HHMMSS, DateUtil.getCurrentDate())
        return lotteries.filter { DateUtil.firstHourIsLessThanSecondHour(currentHour, it.hour ?: "23:59:59") }
    }
}