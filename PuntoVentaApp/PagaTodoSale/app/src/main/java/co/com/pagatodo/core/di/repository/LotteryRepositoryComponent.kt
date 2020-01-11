package co.com.pagatodo.core.di.repository

import co.com.pagatodo.core.data.repositories.VirtualLotteryRepository
import co.com.pagatodo.core.di.print.PrintLotteryModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(PrintLotteryModule::class))
interface VirtualLotteryRepositoryComponent {
    fun inject(virtualLotteryRepository: VirtualLotteryRepository)
}