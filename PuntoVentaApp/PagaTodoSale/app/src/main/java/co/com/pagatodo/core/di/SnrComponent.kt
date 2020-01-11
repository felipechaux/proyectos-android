package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.SnrInteractor
import co.com.pagatodo.core.views.soat.SnrViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
    SnrModule::class,
    UtilModule::class,
    LocalModule::class,
    PrinterStatusModule::class))
interface SnrComponent {
    fun inject(snrViewModel: SnrViewModel)
    fun inject(snrInteractor: SnrInteractor)
}