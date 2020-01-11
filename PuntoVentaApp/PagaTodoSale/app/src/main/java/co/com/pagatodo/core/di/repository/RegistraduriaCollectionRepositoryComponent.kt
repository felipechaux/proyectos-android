package co.com.pagatodo.core.di.repository

import co.com.pagatodo.core.data.repositories.RegistraduriaCollectionRepository
import co.com.pagatodo.core.di.print.PrintRegistraduriaModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(PrintRegistraduriaModule::class))
interface RegistraduriaCollectionRepositoryComponent {
    fun inject(registraduriaCollectionRepository: RegistraduriaCollectionRepository)
}