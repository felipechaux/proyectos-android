package co.com.pagatodo.core.di.print

import co.com.pagatodo.core.data.print.GiroPrint
import co.com.pagatodo.core.data.print.IGiroPrint
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class PrintGiroModule {

    @Singleton
    @Provides
    fun provideGiroPrint(): IGiroPrint {
        return GiroPrint()
    }
}