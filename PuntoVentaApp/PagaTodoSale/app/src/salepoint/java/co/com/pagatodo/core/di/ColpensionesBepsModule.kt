package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.ColpensionesBepsInteractor
import co.com.pagatodo.core.data.interactors.IColpensionesBepsInteractor
import co.com.pagatodo.core.data.repositories.ColpensionesBepsRepository
import co.com.pagatodo.core.data.repositories.IColpensionesBepsRepository
import dagger.Module
import dagger.Provides

@Module
class ColpensionesBepsModule {
    @Provides
    fun provideRepository(): IColpensionesBepsRepository {
        return ColpensionesBepsRepository()
    }

    @Provides
    fun provideInteractor(): IColpensionesBepsInteractor {
        return ColpensionesBepsInteractor()
    }
}