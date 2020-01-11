package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.GiroInteractor
import co.com.pagatodo.core.data.interactors.IGiroInteractor
import co.com.pagatodo.core.data.repositories.GiroRepository
import co.com.pagatodo.core.data.repositories.IGiroRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class GiroModule {

    @Singleton
    @Provides
    fun provideGiroRepository(): IGiroRepository {
        return GiroRepository()
    }

    @Singleton
    @Provides
    fun provideGiroInteractor(): IGiroInteractor {
        return GiroInteractor()
    }

}