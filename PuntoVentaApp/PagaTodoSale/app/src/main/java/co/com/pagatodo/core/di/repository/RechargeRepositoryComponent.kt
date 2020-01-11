package co.com.pagatodo.core.di.repository

import co.com.pagatodo.core.data.repositories.RechargeRepository
import co.com.pagatodo.core.di.LocalModule
import co.com.pagatodo.core.di.print.PrintRechargeModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(PrintRechargeModule::class,LocalModule::class))
interface RechargeRepositoryComponent {
    fun inject(rechargeRepository: RechargeRepository)
}