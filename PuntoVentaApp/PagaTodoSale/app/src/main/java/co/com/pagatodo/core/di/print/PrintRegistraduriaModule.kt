package co.com.pagatodo.core.di.print

import co.com.pagatodo.core.data.print.IRegistraduriaCollectionPrint
import co.com.pagatodo.core.data.print.RegistraduriaCollectionPrint
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PrintRegistraduriaModule {

    @Singleton
    @Provides
    fun provideRegistraduriaPrint(): IRegistraduriaCollectionPrint {
        return RegistraduriaCollectionPrint()
    }
}