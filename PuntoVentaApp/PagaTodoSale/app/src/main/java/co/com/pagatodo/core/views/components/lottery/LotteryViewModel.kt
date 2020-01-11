package co.com.pagatodo.core.views.components.lottery

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.com.pagatodo.core.data.interactors.LotteryInteractor
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.di.DaggerLotteryComponent
import co.com.pagatodo.core.di.LotteryModule
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LotteryViewModel: ViewModel() {

    @Inject
    lateinit var lotteryInteractor: LotteryInteractor

    internal lateinit var lotteries: MutableLiveData<List<LotteryModel>>

    init {

        DaggerLotteryComponent.builder()
            .lotteryModule(LotteryModule())
            .build().inject(this)

        if (!::lotteries.isInitialized) {
            lotteries = MutableLiveData()
        }
    }

    @SuppressLint("CheckResult")
    fun loadLotteriesForChance() {
        lotteryInteractor.getLotteriesForChance()?.subscribe { lotteries.value = it }
    }

    @SuppressLint("CheckResult")
    fun loadLotteriesForSuperChance() {
        lotteryInteractor.getLotteriesForSuperChance()?.subscribe { lotteries.value = it }
    }

    @SuppressLint("CheckResult")
    fun loadLotteriesForPayMillionaire() {
        lotteryInteractor.getLotteriesForPayMillionaire()?.subscribe { lotteries.value = it }
    }

    @SuppressLint("CheckResult")
    fun loadLotteriesForSuperAstro() {
        lotteryInteractor.getLotteriesForSuperAstro()?.subscribe {

            it.forEach { item ->

                item.name =  item.name?.toLowerCase()
                val firstC = item.name?.first()?.toUpperCase()
                val lottery = item.name?.subSequence(1,item.name!!.length)
                item.name =  "$firstC$lottery"

            }

            lotteries.value = it


        }
    }
}