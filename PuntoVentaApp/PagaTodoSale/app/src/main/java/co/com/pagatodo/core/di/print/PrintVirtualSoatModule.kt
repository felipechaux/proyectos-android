package co.com.pagatodo.core.di.print

import co.com.pagatodo.core.data.print.IVirtualSoatPrint
import co.com.pagatodo.core.data.print.VirtualSoatPrint
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PrintVirtualSoatModule {

    @Singleton
    @Provides
    fun provideVirtualSoatPrint(): IVirtualSoatPrint {
        return VirtualSoatPrint()
    }
}