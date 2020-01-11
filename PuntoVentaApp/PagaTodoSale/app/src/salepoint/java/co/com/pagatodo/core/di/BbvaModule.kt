package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.BbvaInteractor
import co.com.pagatodo.core.data.interactors.IBbvaInteractor
import co.com.pagatodo.core.data.interactors.IColpensionesBepsInteractor
import co.com.pagatodo.core.data.repositories.BbvaRepository
import co.com.pagatodo.core.data.repositories.IBbvaRepository
import dagger.Module
import dagger.Provides

@Module
class BbvaModule {

    @Provides
    fun provideRepository(): IBbvaRepository{
        return BbvaRepository()
    }

    @Provides
    fun provideInteractor(): IBbvaInteractor{
        return BbvaInteractor()
    }
}