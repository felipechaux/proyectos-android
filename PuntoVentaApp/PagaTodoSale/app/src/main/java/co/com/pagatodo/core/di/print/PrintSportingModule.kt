package co.com.pagatodo.core.di.print

import co.com.pagatodo.core.data.print.ISportingPrint
import co.com.pagatodo.core.data.print.SportingPrint
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PrintSportingModule {

    @Singleton
    @Provides
    fun provideSportingPrint(): ISportingPrint {
        return SportingPrint()
    }
}