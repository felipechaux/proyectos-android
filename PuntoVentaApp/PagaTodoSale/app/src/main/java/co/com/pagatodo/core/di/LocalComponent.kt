package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.LocalInteractor
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(LocalModule::class))
interface LocalComponent {
    fun inject(localInteractor: LocalInteractor)
}