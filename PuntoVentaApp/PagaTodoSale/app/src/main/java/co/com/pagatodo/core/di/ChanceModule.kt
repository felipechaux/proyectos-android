package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.ChanceInteractor
import co.com.pagatodo.core.data.interactors.IChanceInteractor
import co.com.pagatodo.core.data.repositories.ChanceRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Clase usada en la inyección de dependencias con la librería dagger
 * cumple con la funcionalidad de proveer los objetos necesarios
 */
@Module
class ChanceModule {

    /**
     * Método utilizado para proveer el interactor del producto chance
     */
    @Singleton
    @Provides
    fun provideChanceInteractor(): IChanceInteractor {
        return ChanceInteractor(ChanceRepository())
    }
}