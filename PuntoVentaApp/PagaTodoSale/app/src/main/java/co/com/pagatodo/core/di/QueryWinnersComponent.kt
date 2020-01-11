package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.QueryWinnersInteractor
import co.com.pagatodo.core.data.repositories.QueryWinnersRepository
import co.com.pagatodo.core.views.querywinners.QueryWinnersViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(QueryWinnersModule::class))
interface QueryWinnersComponent {
    fun inject(queryWinnersRepository: QueryWinnersRepository)
    fun inject(queryWinnersInteractor: QueryWinnersInteractor)
    fun inject(queryWinnersViewModel: QueryWinnersViewModel)
}