package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.IRechargeSitpInteractor
import co.com.pagatodo.core.data.interactors.RechargeSitpInteractor
import co.com.pagatodo.core.data.repositories.IRechargeSitpRepository
import co.com.pagatodo.core.data.repositories.RechargeSitpRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RechargeSitpModule {

    @Singleton
    @Provides
    fun provideRechargeSitpInteractor(): IRechargeSitpInteractor{
        return RechargeSitpInteractor()
    }

    @Singleton
    @Provides
    fun provideRechargeSitpRepository(): IRechargeSitpRepository{
        return RechargeSitpRepository()
    }
}