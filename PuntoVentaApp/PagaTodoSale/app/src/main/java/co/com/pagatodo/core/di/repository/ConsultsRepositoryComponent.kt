package co.com.pagatodo.core.di.repository

import co.com.pagatodo.core.data.repositories.CurrentBoxRepository
import co.com.pagatodo.core.di.print.PrintConsultsModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(PrintConsultsModule::class))
interface ConsultsRepositoryComponent {
    fun inject(currentBoxRepository: CurrentBoxRepository)
}