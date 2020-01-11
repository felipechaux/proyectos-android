package co.com.pagatodo.core.di.repository

import co.com.pagatodo.core.data.repositories.ChanceRepository
import co.com.pagatodo.core.di.print.PrintChanceProductModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(PrintChanceProductModule::class))
interface ChanceRepositoryComponent {
    fun inject(chancerepository: ChanceRepository)
}