package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.PayuCollectingInteractor
import co.com.pagatodo.core.views.payu.PayuViewModel
import dagger.Component

@Component(modules = arrayOf(PayuModule::class))
interface PayuComponent {
    fun inject(PayUCollectingInteractor: PayuCollectingInteractor)
    fun inject(PayuViewModel: PayuViewModel)
}