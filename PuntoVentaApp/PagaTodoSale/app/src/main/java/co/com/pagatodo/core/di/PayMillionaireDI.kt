package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.IPayMillionaireInteractor
import co.com.pagatodo.core.data.interactors.PayMillionaireInteractor
import co.com.pagatodo.core.data.repositories.PayMillionaireRepository
import co.com.pagatodo.core.views.paymillionaire.PayMillionaireViewModel
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PayMillionaireModule {

    @Singleton
    @Provides
    fun providePayMillionaireInteractor(): IPayMillionaireInteractor {
        return PayMillionaireInteractor(PayMillionaireRepository())
    }
}

@Singleton
@Component(modules = arrayOf(PayMillionaireModule::class, UtilModule::class,
    LocalModule::class, PrinterStatusModule::class))
interface PayMillionaireComponent {
    fun inject(payMillionaireViewModel: PayMillionaireViewModel)
}