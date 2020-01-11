package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.BbvaInteractor
import co.com.pagatodo.core.views.bbva.BbvaViewModel
import dagger.Component

@Component(modules = arrayOf(BbvaModule::class,LocalModule::class))
interface BbvaComponent {

    fun inject(viewModel: BbvaViewModel)
    fun inject(intercator: BbvaInteractor)

}