package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.ElderlyInteractor
import co.com.pagatodo.core.data.interactors.IElderlyInteractor
import co.com.pagatodo.core.data.repositories.ElderlyRepository
import co.com.pagatodo.core.data.repositories.IElderlyRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ElderlyModule {

    @Singleton
    @Provides
    fun provideElderlyRepository(): IElderlyRepository {
        return ElderlyRepository()
    }

    @Singleton
    @Provides
    fun provideElderlyInteractor(): IElderlyInteractor {
        return ElderlyInteractor()
    }

}