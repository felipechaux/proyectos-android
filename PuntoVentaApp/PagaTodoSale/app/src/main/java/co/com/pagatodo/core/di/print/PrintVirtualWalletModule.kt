package co.com.pagatodo.core.di.print

import co.com.pagatodo.core.data.print.IVirtualWalletPrint
import co.com.pagatodo.core.data.print.VirtualWalletPrint
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class PrintVirtualWalletModule {

    @Singleton
    @Provides
    fun provideVirtualWalletPrint(): IVirtualWalletPrint {
        return VirtualWalletPrint()
    }
}