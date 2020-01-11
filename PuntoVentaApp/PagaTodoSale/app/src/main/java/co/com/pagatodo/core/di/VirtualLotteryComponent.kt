package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.VirtualLotteryInteractor
import co.com.pagatodo.core.data.repositories.VirtualLotteryRepository
import co.com.pagatodo.core.views.lottery.LotterySaleViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(VirtualLotteryModule::class, PrinterStatusModule::class))
interface VirtualLotteryComponent {
    fun inject(virtualLotteryInteractor: VirtualLotteryInteractor)
    fun inject(lotteryViewModel: LotterySaleViewModel)
}