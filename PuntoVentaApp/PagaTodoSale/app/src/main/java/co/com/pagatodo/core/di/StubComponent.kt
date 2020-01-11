package co.com.pagatodo.core.di

import co.com.pagatodo.core.views.stub.StubViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AuthModule::class, LocalModule::class, UtilModule::class))
interface StubComponent {
    fun inject(stubViewModel: StubViewModel)
}