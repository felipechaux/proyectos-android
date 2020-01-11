package co.com.pagatodo.core.di.repository

import co.com.pagatodo.core.data.repositories.MaxiChanceRepository
import co.com.pagatodo.core.di.print.PrintChanceProductModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(PrintChanceProductModule::class))
interface MaxiChanceRepositoryComponent {
    fun inject(maxichancerepository: MaxiChanceRepository)
}