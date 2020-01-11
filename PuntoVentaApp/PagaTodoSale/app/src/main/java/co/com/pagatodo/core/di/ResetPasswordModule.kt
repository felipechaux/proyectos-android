package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.ResetPasswordInteractor
import co.com.pagatodo.core.data.repositories.IResetPasswordRepository
import co.com.pagatodo.core.data.repositories.ResetPasswordRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class ResetPasswordModule {

    @Singleton
    @Provides
    fun provideResetPasswordRepository(): IResetPasswordRepository {
        return ResetPasswordRepository()
    }

    @Singleton
    @Provides
    fun provideResetPasswordInteractor(): ResetPasswordInteractor {
        return ResetPasswordInteractor()
    }
}