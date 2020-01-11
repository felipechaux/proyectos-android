package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.SportingInteractor
import co.com.pagatodo.core.views.sporting.SportingViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(SportingModule::class, PrinterStatusModule::class))
interface SportingComponent {
    fun inject(sportingInteractor: SportingInteractor)
    fun inject(viewModel: SportingViewModel)
}