package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.IVirtualSoatInteractor
import co.com.pagatodo.core.data.interactors.VirtualSoatInteractor
import co.com.pagatodo.core.data.repositories.IVirtualSoatRepository
import co.com.pagatodo.core.data.repositories.VirtualSoatRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class VirtualSoatModule {

    @Singleton
    @Provides
    fun provideInteractor(): IVirtualSoatInteractor {
        return VirtualSoatInteractor()
    }

    @Singleton
    @Provides
    fun provideRepository(): IVirtualSoatRepository {
        return VirtualSoatRepository()
    }
}