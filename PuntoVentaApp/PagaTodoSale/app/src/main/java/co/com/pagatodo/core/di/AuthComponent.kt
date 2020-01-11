package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.AuthInteractor
import co.com.pagatodo.core.data.repositories.AuthRepository
import co.com.pagatodo.core.views.homemenu.MenuViewModel
import co.com.pagatodo.core.views.login.AuthViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
    AuthModule::class,
    GeneralParameterModule::class,
    DBHelperModule::class,
    LotteryModule::class,
    RaffleModule::class,
    LocalModule::class,
    ModalityModule::class,
    RechargeModule::class,
    PromotionModule::class
))
interface AuthComponent {
    fun inject(authInteractor: AuthInteractor)
    fun inject(authRepository: AuthRepository)
    fun inject(authViewModel: AuthViewModel)
    fun inject(menuViewModel: MenuViewModel)
}