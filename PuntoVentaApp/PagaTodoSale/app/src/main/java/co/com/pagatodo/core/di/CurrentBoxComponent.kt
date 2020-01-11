package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.CurrentBoxInteractor
import co.com.pagatodo.core.data.repositories.CurrentBoxRepository
import co.com.pagatodo.core.views.currentbox.CurrentBoxViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(CurrentBoxModule::class, PrinterStatusModule::class, UtilModule::class))
interface CurrentBoxComponent {
    fun inject(currentBoxInteractor: CurrentBoxInteractor)
    fun inject(currentBoxViewModel: CurrentBoxViewModel)
}