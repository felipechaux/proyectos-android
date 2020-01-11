package co.com.pagatodo.core.di.print

import co.com.pagatodo.core.data.print.chance.ChanceProductsPrint
import co.com.pagatodo.core.data.print.chance.IChanceProductsPrint
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PrintChanceProductModule {

    @Singleton
    @Provides
    fun provideChanceProductsPrint(): IChanceProductsPrint {
        return ChanceProductsPrint()
    }
}