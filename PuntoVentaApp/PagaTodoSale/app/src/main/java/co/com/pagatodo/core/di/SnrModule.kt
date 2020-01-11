package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.ISnrInteractor
import co.com.pagatodo.core.data.interactors.SnrInteractor
import co.com.pagatodo.core.data.repositories.ISnrRepository
import co.com.pagatodo.core.data.repositories.SnrRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SnrModule {

    @Singleton
    @Provides
    fun provideSnrRepository(): ISnrRepository {
        return SnrRepository()
    }

    @Singleton
    @Provides
    fun provideSnrInteractor(): ISnrInteractor {
        return SnrInteractor()
    }

}