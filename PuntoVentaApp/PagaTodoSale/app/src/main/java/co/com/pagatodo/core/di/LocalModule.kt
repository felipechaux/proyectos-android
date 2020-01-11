package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.ILocalInteractor
import co.com.pagatodo.core.data.interactors.LocalInteractor
import co.com.pagatodo.core.data.repositories.local.ILocalRepository
import co.com.pagatodo.core.data.repositories.local.LocalRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocalModule {

    @Singleton
    @Provides
    fun provideLocalRepository(): ILocalRepository {
        return LocalRepository()
    }

    @Singleton
    @Provides
    fun provideLocalInteractor(): ILocalInteractor {
        return LocalInteractor()
    }
}