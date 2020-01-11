package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.GeneralParameterInteractor
import co.com.pagatodo.core.data.repositories.generalparameters.GeneralParametersRepository
import co.com.pagatodo.core.data.repositories.generalparameters.IGeneralParametersRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class GeneralParameterModule {

    @Singleton
    @Provides
    fun provideGeneralParameterRepository(): IGeneralParametersRepository {
        return GeneralParametersRepository()
    }

    @Singleton
    @Provides
    fun provideGeneralParameterInteractor(): GeneralParameterInteractor {
        return GeneralParameterInteractor()
    }
}