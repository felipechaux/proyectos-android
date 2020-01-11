package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.MaxiChanceInteractor
import co.com.pagatodo.core.data.repositories.IMaxiChanceRepository
import co.com.pagatodo.core.data.repositories.MaxiChanceRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MaxiChanceModule {

    @Singleton
    @Provides
    fun provideMaxiChanceRepository(): IMaxiChanceRepository {
        return MaxiChanceRepository()
    }

    @Singleton
    @Provides
    fun provideMaxiChanceInteractor(): MaxiChanceInteractor {
        return MaxiChanceInteractor()
    }
}