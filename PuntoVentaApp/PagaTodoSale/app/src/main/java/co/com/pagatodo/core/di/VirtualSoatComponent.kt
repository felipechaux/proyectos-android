package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.VirtualSoatInteractor
import co.com.pagatodo.core.views.soat.VirtualSoatViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(VirtualSoatModule::class,
    LocalModule::class,
    PrinterStatusModule::class))
interface VirtualSoatComponent {
    fun inject(virtualSoatViewModel: VirtualSoatViewModel)
    fun inject(virtualSoatInteractor: VirtualSoatInteractor)

}