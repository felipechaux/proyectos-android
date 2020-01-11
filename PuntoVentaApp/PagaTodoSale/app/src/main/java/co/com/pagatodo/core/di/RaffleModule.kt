package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.RaffleInteractor
import co.com.pagatodo.core.data.repositories.IRaffleRepository
import co.com.pagatodo.core.data.repositories.RaffleRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RaffleModule {

    @Singleton
    @Provides
    fun provideRaffleInteractor(): RaffleInteractor {
        return RaffleInteractor()
    }

    @Singleton
    @Provides
    fun  provideRaffleRepositoy(): IRaffleRepository {
        return RaffleRepository()
    }
}