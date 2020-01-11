package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.QueryInteractor
import co.com.pagatodo.core.data.repositories.LotteryRepository
import co.com.pagatodo.core.data.repositories.QueryRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class QueryModule {

    @Singleton
    @Provides
    fun provideQueryInteractor(): QueryInteractor {
        return QueryInteractor(QueryRepository(), LotteryRepository())
    }
}