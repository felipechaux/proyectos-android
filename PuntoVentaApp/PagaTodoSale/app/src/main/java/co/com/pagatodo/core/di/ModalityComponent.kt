package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.ModalityInteractor
import co.com.pagatodo.core.data.repositories.ModalityRepository
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ModalityModule::class))
interface ModalityComponent {
    fun inject(modalityInteractor: ModalityInteractor)
    fun inject(modalityRepository: ModalityRepository)
}