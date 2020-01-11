package co.com.pagatodo.core.di.repository

import co.com.pagatodo.core.data.repositories.SuperChanceRepository
import co.com.pagatodo.core.di.print.PrintChanceProductModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(PrintChanceProductModule::class))
interface SuperChanceRepositoryComponent {
    fun inject(superchancerepository: SuperChanceRepository)
}