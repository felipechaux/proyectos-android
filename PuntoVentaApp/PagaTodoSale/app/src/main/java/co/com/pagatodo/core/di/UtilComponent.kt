package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.UtilInteractor
import co.com.pagatodo.core.views.stub.StubViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(UtilModule::class))
interface UtilComponent {
    fun inject(utilInteractor: UtilInteractor)
}