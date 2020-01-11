package co.com.pagatodo.core.di
import co.com.pagatodo.core.data.interactors.RechargeSitpInteractor
import co.com.pagatodo.core.data.repositories.RechargeSitpRepository
import co.com.pagatodo.core.views.sitp.RechargeSitpViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(RechargeSitpModule::class))
interface RechargeSitpComponent {
    fun inject(rechangeSitpInteractor: RechargeSitpInteractor)
    fun inject(rechargeSitpViewModel: RechargeSitpViewModel)
}
