package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.SuperAstroInteractor
import co.com.pagatodo.core.data.repositories.SuperAstroRepository
import co.com.pagatodo.core.views.superastro.SuperAstroViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(SuperAstroModule::class, UtilModule::class, PrinterStatusModule::class))
interface SuperAstroComponent {
    fun inject(superAstroInteractor: SuperAstroInteractor)
    fun inject(superAstroViewModel: SuperAstroViewModel)
}