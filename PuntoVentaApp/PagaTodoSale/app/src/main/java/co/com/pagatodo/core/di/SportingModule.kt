package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.SportingInteractor
import co.com.pagatodo.core.data.repositories.ISportingRepository
import co.com.pagatodo.core.data.repositories.SportingRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SportingModule {

    @Singleton
    @Provides
    fun provideLeague14Repository(): ISportingRepository {
        return SportingRepository()
    }

    @Singleton
    @Provides
    fun provideLeague14Interactor(): SportingInteractor {
        return SportingInteractor()
    }
}