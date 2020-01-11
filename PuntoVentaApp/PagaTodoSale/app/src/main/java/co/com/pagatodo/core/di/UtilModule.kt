package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.IUtilInteractor
import co.com.pagatodo.core.data.interactors.UtilInteractor
import co.com.pagatodo.core.data.repositories.IUtilRepository
import co.com.pagatodo.core.data.repositories.UtilRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UtilModule {

    @Singleton
    @Provides
    fun provideUtilInteractor(): IUtilInteractor {
        return UtilInteractor()
    }

    @Singleton
    @Provides
    fun provideUtilRepository(): IUtilRepository {
        return UtilRepository()
    }
}