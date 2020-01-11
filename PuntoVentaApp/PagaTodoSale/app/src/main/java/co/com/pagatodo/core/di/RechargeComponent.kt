package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.LocalInteractor
import co.com.pagatodo.core.data.interactors.RechargeInteractor
import co.com.pagatodo.core.views.recharge.RechargeViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(RechargeModule::class, PrinterStatusModule::class,LocalModule::class,UtilModule::class))
interface RechargeComponent {
    fun inject(rechargeInteractor: RechargeInteractor)
    fun inject(viewModel: RechargeViewModel)
    fun inject (localInteractor: LocalInteractor)
}