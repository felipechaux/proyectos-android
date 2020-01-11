package co.com.pagatodo.core.di.repository

import co.com.pagatodo.core.data.repositories.SportingRepository
import co.com.pagatodo.core.di.print.PrintSportingModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(PrintSportingModule::class))
interface SportingRepositoryComponent {
    fun inject(sportingRepository: SportingRepository)
}