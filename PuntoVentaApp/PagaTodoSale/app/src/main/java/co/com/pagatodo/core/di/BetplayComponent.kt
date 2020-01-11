package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.BetplayInteractor
import co.com.pagatodo.core.data.repositories.BetplayRepository
import co.com.pagatodo.core.views.betplay.BetplayViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(BetplayModule::class, PrinterStatusModule::class))
interface BetplayComponent {
    fun inject(betplayInteractor: BetplayInteractor)
    fun inject(betplayRepository: BetplayRepository)
    fun inject(betplayViewModel: BetplayViewModel)
}