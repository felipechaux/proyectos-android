package co.com.pagatodo.core.di.repository

import co.com.pagatodo.core.data.repositories.VirtualWalletRepository
import co.com.pagatodo.core.di.print.PrintVirtualWalletModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(PrintVirtualWalletModule::class))
interface VirtualWalletRepositoryComponent {
    fun inject(virtualWalletRepository: VirtualWalletRepository)
}