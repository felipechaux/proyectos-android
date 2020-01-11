package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.RechargeInteractor
import co.com.pagatodo.core.data.repositories.IRechargeRepository
import co.com.pagatodo.core.data.repositories.RechargeRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RechargeModule {

    @Singleton
    @Provides
    fun provideRechargeRepository(): IRechargeRepository {
        return RechargeRepository()
    }

    @Singleton
    @Provides
    fun provideRechargeInteractor(): RechargeInteractor {
        return RechargeInteractor()
    }
}