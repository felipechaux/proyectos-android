package co.com.pagatodo.core.di

import co.com.pagatodo.core.views.chance.ChanceViewModel
import dagger.Component
import javax.inject.Singleton

/**
 * Clase usada en la inyección de dependencias con la librería dagger
 * cumple con la función de inyectar los obtetos requeridos para el producto chance
 * y recibe como parámetro el módulo de dicho producto
 */
@Singleton
@Component(modules = arrayOf(ChanceModule::class, UtilModule::class, LocalModule::class, PrinterStatusModule::class))
interface ChanceComponent {
    fun inject(chanceViewModel: ChanceViewModel)
}