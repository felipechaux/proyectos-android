package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.CurrentBoxInteractor
import co.com.pagatodo.core.data.repositories.CurrentBoxRepository
import co.com.pagatodo.core.data.repositories.ICurrentBoxRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CurrentBoxModule {

    @Singleton
    @Provides
    fun provideCurrentBoxRepository(): ICurrentBoxRepository {
        return CurrentBoxRepository()
    }

    @Singleton
    @Provides
    fun provideCurrentBoxInteractor(): CurrentBoxInteractor {
        return CurrentBoxInteractor()
    }
}