package co.com.pagatodo.core.di.repository

import co.com.pagatodo.core.data.repositories.RaffleRepository
import co.com.pagatodo.core.di.print.PrintRaffleModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(PrintRaffleModule::class))
interface RaffleRepositoryComponent {
    fun inject(raffleRepository: RaffleRepository)
}