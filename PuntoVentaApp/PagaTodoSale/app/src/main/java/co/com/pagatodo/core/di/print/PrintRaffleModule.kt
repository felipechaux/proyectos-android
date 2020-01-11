package co.com.pagatodo.core.di.print

import co.com.pagatodo.core.data.print.IRafflePrint
import co.com.pagatodo.core.data.print.RafflePrint

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PrintRaffleModule {

    @Singleton
    @Provides
    fun provideRafflePrint(): IRafflePrint {
        return RafflePrint()
    }
}