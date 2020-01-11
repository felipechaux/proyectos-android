package co.com.pagatodo.core.di.repository

import co.com.pagatodo.core.data.repositories.GiroRepository
import co.com.pagatodo.core.di.print.PrintGiroModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(PrintGiroModule::class))
interface GiroRepositoryComponent {
    fun inject(giroRepository: GiroRepository)
}