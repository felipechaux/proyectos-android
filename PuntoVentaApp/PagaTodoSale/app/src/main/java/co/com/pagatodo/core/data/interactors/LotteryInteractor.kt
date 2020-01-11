package co.com.pagatodo.core.data.interactors

import android.annotation.SuppressLint
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.data.database.DatabaseViewModel
import co.com.pagatodo.core.data.database.entities.LotteryEntityRoom
import co.com.pagatodo.core.data.database.entities.ProductLotteriesEntityRoom
import co.com.pagatodo.core.data.dto.GeneralProductDTO
import co.com.pagatodo.core.data.dto.LotteryDTO
import co.com.pagatodo.core.data.repositories.ILotteryRepository
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.di.DaggerLotteryComponent
import co.com.pagatodo.core.di.LotteryModule
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LotteryInteractor {

    @Inject
    lateinit var lotteryRepository: ILotteryRepository

    constructor() {
        DaggerLotteryComponent
            .builder()
            .lotteryModule(LotteryModule())
            .build().inject(this)
    }

    constructor(lotteryRepository: ILotteryRepository) {
        this.lotteryRepository = lotteryRepository
    }

    fun getLotteriesForChance(): Observable<List<LotteryModel>>? {
        return convertObservableEntityRoomToModel(lotteryRepository.getLotteriesForChanceRoom())
    }

    fun getLotteriesForSuperChance(): Observable<List<LotteryModel>>? {
        return convertObservableEntityRoomToModel(lotteryRepository.getLotteriesForSuperChanceRoom())
    }

    fun getLotteriesForPayMillionaire(): Observable<List<LotteryModel>>? {
        return convertObservableEntityRoomToModel(lotteryRepository.getLotteriesForPayMillionaireRoom())
    }

    fun getLotteriesForSuperAstro(): Observable<List<LotteryModel>>? {
        return convertObservableEntityRoomToModel(lotteryRepository.getLotteriesForSuperAstroRoom())
    }

    @SuppressLint("CheckResult")
    fun saveLotteriesInLocal(lotteriesDTO: List<LotteryDTO>?){
        val lotteryesEntity = arrayListOf<LotteryEntityRoom>()
        lotteriesDTO?.forEach {
            val entity = LotteryEntityRoom(
                it.code ?: "-1",
                it.name,
                it.fullName,
                it.date,
                it.hour,
                it.lotteryDay,
                it.numberFigures,
                it.serieFigure
            )
            lotteryesEntity.add(entity)
        }

        lotteryRepository.saveLotteriesInLocalRoom(lotteryesEntity).subscribe {
            DatabaseViewModel.database.onNext(DatabaseViewModel.DatabaseEvents.LOTTERIES_MASTER_ADDESD)
        }
    }

    fun saveProductLotteriesInLocal(products: List<GeneralProductDTO>?) {
        lotteryRepository.deleteAllProductLotteries()
        products?.forEach {
            val lotteries = it.lotteries
            val productLotteries = arrayListOf<ProductLotteriesEntityRoom>()
            lotteries?.forEach { lottery ->
                val entity = ProductLotteriesEntityRoom(
                    it.code ?: "-1",
                    lottery.code ?: "-1",
                    lottery.fullName,
                    lottery.name,
                    lottery.date,
                    lottery.hour,
                    lottery.fractions,
                    lottery.fractionValue,
                    lottery.draw,
                    lottery.award
                )
                productLotteries.add(entity)
            }

            if (productLotteries.count() > 0) {
                lotteryRepository.saveProductLotteriesInLocal(productLotteries).subscribe {
                    DatabaseViewModel.database.onNext(DatabaseViewModel.DatabaseEvents.CHANCE_LOTTERIES_ADDED)
                }
            }
        }
    }

    private fun convertObservableEntityToModel(observable: Observable<List<LotteryEntityRoom>>?): Observable<List<LotteryModel>>? {
        return observable?.flatMap {
            val response = mutableListOf<LotteryModel>()
            for (ent in it) {
                val lotteryModel = LotteryModel().apply {
                    code = ent.code
                    name = ent.name
                    date = ent.date
                    hour = ent.hour
                    lotteryDay = ent.lotteryDay
                }
                response.add(lotteryModel)
            }
            Observable.just(response)
        }
    }

    private fun convertObservableEntityRoomToModel(observable: Observable<List<LotteryEntityRoom>>?): Observable<List<LotteryModel>>? {
        return observable?.flatMap {
            val response = mutableListOf<LotteryModel>()
            for (ent in it) {
                val lotteryModel = LotteryModel().apply {
                    code = ent.code
                    name = ent.name
                    fullName = ent.fullName
                    date = ent.date
                    hour = ent.hour
                    lotteryDay = ent.lotteryDay
                }
                response.add(lotteryModel)
            }
            Observable.just(response)
        }
    }
}