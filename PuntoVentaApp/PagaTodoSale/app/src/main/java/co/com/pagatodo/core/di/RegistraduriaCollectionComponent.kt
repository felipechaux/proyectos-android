package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.RegistraduriaCollectionInteractor
import co.com.pagatodo.core.views.registraduria.RegistraduriaCollectionViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
    RegistraduriaCollectionModule::class,
    UtilModule::class,
    LocalModule::class,
    PrinterStatusModule::class))
interface RegistraduriaCollectionComponent {
    fun inject(registraduriaCollectionViewModel: RegistraduriaCollectionViewModel)
    fun inject(registraduriaCollectionInteractor: RegistraduriaCollectionInteractor)
}