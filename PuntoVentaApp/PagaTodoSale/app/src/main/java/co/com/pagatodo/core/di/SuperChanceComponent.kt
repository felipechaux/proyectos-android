package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.SuperChanceInteractor
import co.com.pagatodo.core.data.repositories.SuperChanceRepository
import co.com.pagatodo.core.di.print.PrintChanceProductModule
import co.com.pagatodo.core.views.superchance.SuperChanceViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(SuperChanceModule::class, UtilModule::class, PrinterStatusModule::class))
interface SuperChanceComponent {
    fun inject(superChanceInteractor: SuperChanceInteractor)
    fun inject(superChanceViewModel: SuperChanceViewModel)
}