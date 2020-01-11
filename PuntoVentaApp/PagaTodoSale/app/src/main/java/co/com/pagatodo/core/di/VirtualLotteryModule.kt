package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.VirtualLotteryInteractor
import co.com.pagatodo.core.data.repositories.IVirtualLotteryRepository
import co.com.pagatodo.core.data.repositories.VirtualLotteryRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class VirtualLotteryModule {

    @Singleton
    @Provides
    fun provideVirtualLotteryRepository(): IVirtualLotteryRepository {
        return VirtualLotteryRepository()
    }

    @Singleton
    @Provides
    fun provideVirtualLotteryInteractor(): VirtualLotteryInteractor {
        return VirtualLotteryInteractor()
    }
}