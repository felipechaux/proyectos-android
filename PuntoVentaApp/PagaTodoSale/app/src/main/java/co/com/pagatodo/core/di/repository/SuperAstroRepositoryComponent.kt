package co.com.pagatodo.core.di.repository

import co.com.pagatodo.core.data.repositories.SuperAstroRepository
import co.com.pagatodo.core.di.print.PrintSuperAstroModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(PrintSuperAstroModule::class))
interface SuperAstroRepositoryComponent {
    fun inject(superAstroRepository: SuperAstroRepository)
}