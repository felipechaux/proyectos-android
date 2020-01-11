package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.WalletInteractor
import co.com.pagatodo.core.data.repositories.IWalletRepository
import co.com.pagatodo.core.data.repositories.WalletRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class WalletModule {

    @Singleton
    @Provides
    fun provideWalletInteractor(): WalletInteractor {
        return WalletInteractor()
    }

    @Singleton
    @Provides
    fun  provideWalletRepositoy(): IWalletRepository {
        return WalletRepository()
    }
}