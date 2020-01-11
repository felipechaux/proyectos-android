package co.com.pagatodo.core.di.print

import co.com.pagatodo.core.data.print.IRechargePrint
import co.com.pagatodo.core.data.print.ISportingPrint
import co.com.pagatodo.core.data.print.RechargePrint
import co.com.pagatodo.core.data.print.SportingPrint
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PrintRechargeModule {

    @Singleton
    @Provides
    fun provideRechargePrint(): IRechargePrint {
        return RechargePrint()
    }
}