package co.com.pagatodo.core.di.repository

import co.com.pagatodo.core.data.repositories.BalotoRepository
import co.com.pagatodo.core.data.repositories.LotteryRepository
import co.com.pagatodo.core.di.print.PrintBalotoModule
import co.com.pagatodo.core.di.print.PrintLotteryModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(PrintBalotoModule::class))
interface BalotoRepositoryComponent {
    fun inject(balotoRepository: BalotoRepository)
}