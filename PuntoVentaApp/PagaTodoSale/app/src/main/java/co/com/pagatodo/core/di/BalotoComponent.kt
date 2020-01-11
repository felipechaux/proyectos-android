package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.BalotoInteractor
import co.com.pagatodo.core.data.repositories.BalotoRepository
import co.com.pagatodo.core.views.baloto.BalotoViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(BalotoModule::class, PrinterStatusModule::class))
interface BalotoComponent {
    fun inject(balotoInteractor: BalotoInteractor)
    fun inject(balotoViewModel: BalotoViewModel)
}