package co.com.pagatodo.core.di.repository

import co.com.pagatodo.core.data.repositories.PayMillionaireRepository
import co.com.pagatodo.core.di.print.PrintChanceProductModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(PrintChanceProductModule::class))
interface MillionairePaymentRepositoryComponent {
    fun inject(paymillionairepayment: PayMillionaireRepository)
}