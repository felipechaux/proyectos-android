package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.ElderlyInteractor
import co.com.pagatodo.core.views.elderly.ElderlyViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
    ElderlyModule::class,
    UtilModule::class,
    LocalModule::class,
    PrinterStatusModule::class,
    GiroModule::class))

interface ElderlyComponent {
    fun inject(elderlyViewModel: ElderlyViewModel)
    fun inject(elderlyInteractor: ElderlyInteractor)
}