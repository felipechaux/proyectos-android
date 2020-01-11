package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.BetplayInteractor
import co.com.pagatodo.core.data.repositories.BetplayRepository
import co.com.pagatodo.core.data.repositories.IBetplayRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BetplayModule {

    @Singleton
    @Provides
    fun BetplayRechargeRepository(): IBetplayRepository {
        return BetplayRepository()
    }

    @Singleton
    @Provides
    fun provideBetplayInteractor(): BetplayInteractor {
        return BetplayInteractor()
    }
}