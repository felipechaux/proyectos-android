package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.MaxiChanceInteractor
import co.com.pagatodo.core.views.maxichance.MaxiChanceViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(MaxiChanceModule::class, UtilModule::class, LocalModule::class, PrinterStatusModule::class))
interface MaxiChanceComponent {
    fun inject(maxiChanceInteractor: MaxiChanceInteractor)
    fun inject(maxiChanceViewModel: MaxiChanceViewModel)
}