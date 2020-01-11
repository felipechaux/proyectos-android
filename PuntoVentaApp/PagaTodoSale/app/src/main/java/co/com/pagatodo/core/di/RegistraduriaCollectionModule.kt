package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.IRegistraduriaCollectionInteractor
import co.com.pagatodo.core.data.interactors.RegistraduriaCollectionInteractor
import co.com.pagatodo.core.data.repositories.IRegistraduriaCollectionRepository
import co.com.pagatodo.core.data.repositories.RegistraduriaCollectionRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RegistraduriaCollectionModule {

    @Singleton
    @Provides
    fun provideRegistraduriaCollectionRepository(): IRegistraduriaCollectionRepository {
        return RegistraduriaCollectionRepository()
    }

    @Singleton
    @Provides
    fun provideRegistraduriaCollectionInteractor(): IRegistraduriaCollectionInteractor {
        return RegistraduriaCollectionInteractor()
    }

}