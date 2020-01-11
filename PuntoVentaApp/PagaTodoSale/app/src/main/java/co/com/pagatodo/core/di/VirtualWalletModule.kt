package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.IVitualWalletInteractor
import co.com.pagatodo.core.data.interactors.VitualWalletInteractor
import co.com.pagatodo.core.data.repositories.IVirtualWalletRepository
import co.com.pagatodo.core.data.repositories.VirtualWalletRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class VirtualWalletModule {

    @Singleton
    @Provides
    fun provideRepository(): IVirtualWalletRepository{
        return VirtualWalletRepository()
    }

    @Singleton
    @Provides
    fun provideInteractor(): IVitualWalletInteractor{
        return VitualWalletInteractor()
    }
}