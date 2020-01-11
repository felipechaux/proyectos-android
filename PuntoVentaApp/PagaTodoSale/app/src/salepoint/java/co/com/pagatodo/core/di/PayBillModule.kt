package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.IPayBillInteractor
import co.com.pagatodo.core.data.interactors.PayBillInteractor
import co.com.pagatodo.core.data.repositories.IPayBillRepository
import co.com.pagatodo.core.data.repositories.PayBillRepository
import dagger.Module
import dagger.Provides

@Module
class PayBillModule {
    @Provides
    fun providerPayBillRepository(): IPayBillRepository {
        return PayBillRepository()
    }

    @Provides
    fun providerPayBillInteractor(): IPayBillInteractor {
        return PayBillInteractor()
    }
}