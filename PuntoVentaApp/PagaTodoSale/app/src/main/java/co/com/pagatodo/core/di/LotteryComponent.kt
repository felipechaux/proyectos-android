package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.LotteryInteractor
import co.com.pagatodo.core.data.repositories.LotteryRepository
import co.com.pagatodo.core.views.components.lottery.LotteryViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(LotteryModule::class, PrinterStatusModule::class))
interface LotteryComponent {

    fun inject(lotteryInteractor: LotteryInteractor)
    fun inject(lotteryViewModel: LotteryViewModel)
}