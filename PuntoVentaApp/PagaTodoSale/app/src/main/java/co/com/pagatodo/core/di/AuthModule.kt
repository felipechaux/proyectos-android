package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.AuthInteractor
import co.com.pagatodo.core.data.interactors.IAuthInteractor
import co.com.pagatodo.core.data.repositories.AuthRepository
import co.com.pagatodo.core.data.repositories.IAuthRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AuthModule {

    @Singleton
    @Provides
    fun provideAuthInteractor(): IAuthInteractor {
        return AuthInteractor()
    }

    @Singleton
    @Provides
    fun provideAuthRepository(): IAuthRepository {
        return AuthRepository()
    }
}
