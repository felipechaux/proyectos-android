package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.VitualWalletInteractor
import co.com.pagatodo.core.views.virtualwallet.VirtualWalletViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = arrayOf(
        VirtualWalletModule::class,
        PrinterStatusModule::class
    )
)
interface VirtualWalletComponent {
    fun inject(virtualWalletViewModel: VirtualWalletViewModel)
    fun inject(virtualWalletInteractor: VitualWalletInteractor)
}