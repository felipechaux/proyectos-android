package co.com.pagatodo.core.di.repository

import co.com.pagatodo.core.data.repositories.VirtualSoatRepository
import co.com.pagatodo.core.di.print.PrintVirtualSoatModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(PrintVirtualSoatModule::class))
interface VirtualSoatRepositoryComponent {
    fun inject(virtualSoatRepository: VirtualSoatRepository)
}