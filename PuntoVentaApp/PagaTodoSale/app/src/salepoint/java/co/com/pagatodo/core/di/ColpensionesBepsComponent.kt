package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.ColpensionesBepsInteractor
import co.com.pagatodo.core.views.colpensionesbeps.ColpensionesBepsViewModel
import dagger.Component

@Component(modules = arrayOf(ColpensionesBepsModule::class,LocalModule::class))
interface ColpensionesBepsComponent {
    fun inject(viewModel: ColpensionesBepsViewModel)
    fun inject(intercator: ColpensionesBepsInteractor)
}