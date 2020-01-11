package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.database.DBHelperResponse
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DBHelperModule {

    @Singleton
    @Provides
    fun provideDBHelper(): DBHelperResponse {
        return DBHelperResponse()
    }
}