package co.com.pagatodo.core.di.print

import co.com.pagatodo.core.data.print.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PrintBalotoModule {

    @Singleton
    @Provides
    fun provideBalotoPrint(): IBalotoPrint {
        return BalotoPrint()
    }
}