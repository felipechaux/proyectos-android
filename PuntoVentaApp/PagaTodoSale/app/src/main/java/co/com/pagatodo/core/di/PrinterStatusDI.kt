package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.print.IPrinterStatusInfo
import co.com.pagatodo.core.data.print.PrinterStatusInfo
import co.com.pagatodo.core.views.base.BaseActivity
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PrinterStatusModule {

    @Singleton
    @Provides
    fun providePrinterStatusInfo(): IPrinterStatusInfo {
        return PrinterStatusInfo()
    }
}