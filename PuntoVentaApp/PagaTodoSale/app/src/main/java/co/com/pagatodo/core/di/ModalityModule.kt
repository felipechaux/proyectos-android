package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.ModalityInteractor
import co.com.pagatodo.core.data.repositories.IModalityRepository
import co.com.pagatodo.core.data.repositories.ModalityRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ModalityModule {

    @Singleton
    @Provides
    fun provideModalityRepository(): IModalityRepository {
        return ModalityRepository()
    }

    @Singleton
    @Provides
    fun provideModalityInteractor(): ModalityInteractor {
        return ModalityInteractor()
    }
}