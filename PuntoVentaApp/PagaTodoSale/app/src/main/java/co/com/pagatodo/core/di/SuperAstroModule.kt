package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.SuperAstroInteractor
import co.com.pagatodo.core.data.repositories.ISuperAstroRepository
import co.com.pagatodo.core.data.repositories.SuperAstroRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class SuperAstroModule {

    @Singleton
    @Provides
    fun provideSuperAstroRepository(): ISuperAstroRepository {
        return SuperAstroRepository()
    }

    @Singleton
    @Provides
    fun provideRechargeInteractor(): SuperAstroInteractor {
        return SuperAstroInteractor()
    }
}