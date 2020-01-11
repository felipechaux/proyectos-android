package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.IPayuCollectingInteractor
import co.com.pagatodo.core.data.interactors.PayuCollectingInteractor
import co.com.pagatodo.core.data.repositories.IPayuRepository
import co.com.pagatodo.core.data.repositories.PayuRepository
import dagger.Module
import dagger.Provides

@Module
class PayuModule {

    @Provides
    fun  providePayUCollectingInteractor(): IPayuCollectingInteractor {
        return PayuCollectingInteractor()
    }

    @Provides
    fun  providePayUCollectingRepositoy(): IPayuRepository {
        return PayuRepository()
    }
}