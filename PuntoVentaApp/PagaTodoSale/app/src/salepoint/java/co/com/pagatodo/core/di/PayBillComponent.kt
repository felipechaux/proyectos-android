package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.PayBillInteractor
import co.com.pagatodo.core.views.paybills.PayBillViewModel
import dagger.Component

@Component (modules = arrayOf(PayBillModule::class))
interface PayBillComponent {
    fun inject(payBillViewModel: PayBillViewModel)
    fun inject(payBillInteractor: PayBillInteractor)
}