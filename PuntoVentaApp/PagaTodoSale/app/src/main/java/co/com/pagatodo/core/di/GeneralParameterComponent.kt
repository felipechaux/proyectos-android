package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.GeneralParameterInteractor
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(GeneralParameterModule::class))
interface GeneralParameterComponent {
    fun inject(generalParameterInteractor: GeneralParameterInteractor)
}