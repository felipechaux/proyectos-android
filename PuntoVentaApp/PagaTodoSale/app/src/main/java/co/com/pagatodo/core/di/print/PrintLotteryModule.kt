package co.com.pagatodo.core.di.print

import co.com.pagatodo.core.data.print.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PrintLotteryModule {

    @Singleton
    @Provides
    fun provideLotteryPrint(): ILotteriesPrint {
        return LotteriesPrint()
    }
}