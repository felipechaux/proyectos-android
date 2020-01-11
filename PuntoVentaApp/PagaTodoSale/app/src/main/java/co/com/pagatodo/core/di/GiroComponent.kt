package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.GiroInteractor
import co.com.pagatodo.core.views.giro.GiroViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
    GiroModule::class,
    UtilModule::class,
    LocalModule::class,
    PrinterStatusModule::class))
interface GiroComponent {
    fun inject(giroViewModel: GiroViewModel)
    fun inject(giroInteractor: GiroInteractor)
}