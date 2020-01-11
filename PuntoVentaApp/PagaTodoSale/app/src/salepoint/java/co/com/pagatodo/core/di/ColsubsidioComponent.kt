package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.ColsubsidioCollectionInteractor
import co.com.pagatodo.core.views.colsubsidio.ColsubsidioCollectionViewModel
import dagger.Component

@Component(modules = arrayOf(
    ColsubsidioModule::class,
    UtilModule::class,
    LocalModule::class,
    PrinterStatusModule::class))
interface ColsubsidioComponent {
    fun inject(colsubsidioCollectionViewModel: ColsubsidioCollectionViewModel)
    fun inject(colsubsidioCollectionInteractor: ColsubsidioCollectionInteractor)
}