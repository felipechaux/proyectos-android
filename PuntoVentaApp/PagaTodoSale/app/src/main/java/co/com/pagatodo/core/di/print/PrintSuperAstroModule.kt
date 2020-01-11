package co.com.pagatodo.core.di.print

import co.com.pagatodo.core.data.print.ISuperAstroPrint
import co.com.pagatodo.core.data.print.SuperAstroPrint
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PrintSuperAstroModule {

    @Singleton
    @Provides
    fun provideSuperAstroPrint(): ISuperAstroPrint {
        return SuperAstroPrint()
    }
}