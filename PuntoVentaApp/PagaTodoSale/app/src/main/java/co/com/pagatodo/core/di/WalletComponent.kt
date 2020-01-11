package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.WalletInteractor
import co.com.pagatodo.core.views.wallet.WalletViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(WalletModule::class))
interface WalletComponent {
    fun inject(walletInteractor: WalletInteractor)
    fun inject(walletViewModel: WalletViewModel)
}