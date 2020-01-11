package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.BalotoInteractor
import co.com.pagatodo.core.data.repositories.BalotoRepository
import co.com.pagatodo.core.data.repositories.IBalotoRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BalotoModule {

    @Singleton
    @Provides
    fun provideBalotoRepository(): IBalotoRepository {
        return BalotoRepository()
    }

    @Singleton
    @Provides
    fun provideBalotoInteractor(): BalotoInteractor {
        return BalotoInteractor()
    }
}