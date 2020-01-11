package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.RaffleInteractor
import co.com.pagatodo.core.views.raffle.RaffleViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(RaffleModule::class, PrinterStatusModule::class))
interface RaffleComponent {
    fun inject(raffleInteractor: RaffleInteractor)
    fun inject(raffleViewModel: RaffleViewModel)
}