package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.QueryWinnersInteractor
import co.com.pagatodo.core.data.repositories.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class QueryWinnersModule {

    @Singleton
    @Provides
    fun provideQueryWinnersInteractor(): QueryWinnersInteractor {
        return QueryWinnersInteractor()
    }

    @Singleton
    @Provides
    fun  provideQueryWinnersRepositoy(): IQueryWinnersRespository {
        return QueryWinnersRepository()
    }
}
