package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.SuperChanceInteractor
import co.com.pagatodo.core.data.repositories.ISuperChanceRepository
import co.com.pagatodo.core.data.repositories.SuperChanceRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SuperChanceModule {

    @Singleton
    @Provides
    fun provideSuperChanceRepository(): ISuperChanceRepository {
        return SuperChanceRepository()
    }

    @Singleton
    @Provides
    fun provideSuperChanceInteractor(): SuperChanceInteractor {
        return SuperChanceInteractor()
    }
}