package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.LotteryInteractor
import co.com.pagatodo.core.data.repositories.ILotteryRepository
import co.com.pagatodo.core.data.repositories.LotteryRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LotteryModule {

    @Singleton
    @Provides
    fun provideLotteryRepository(): ILotteryRepository {
        return LotteryRepository()
    }

    @Singleton
    @Provides
    fun provideLotteryInteractor(): LotteryInteractor {
        return LotteryInteractor()
    }
}