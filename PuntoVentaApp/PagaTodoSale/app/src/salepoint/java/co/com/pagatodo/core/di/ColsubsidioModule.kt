package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.ColsubsidioCollectionInteractor
import co.com.pagatodo.core.data.interactors.IColsubsidioCollectionInteractor
import co.com.pagatodo.core.data.repositories.ColsubsidioCollectionRepository
import co.com.pagatodo.core.data.repositories.IColsubsidioCollectionRepository
import dagger.Module
import dagger.Provides

@Module
class ColsubsidioModule {

    @Provides
    fun provideColsubsidioCollectionRepository(): IColsubsidioCollectionRepository {
        return ColsubsidioCollectionRepository()
    }

    @Provides
    fun provideColsubsidioCollectionInteractor(): IColsubsidioCollectionInteractor {
        return ColsubsidioCollectionInteractor()
    }

}