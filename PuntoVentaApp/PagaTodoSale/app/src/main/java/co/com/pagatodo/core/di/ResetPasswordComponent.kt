package co.com.pagatodo.core.di

import co.com.pagatodo.core.data.interactors.ResetPasswordInteractor
import co.com.pagatodo.core.data.repositories.ResetPasswordRepository
import co.com.pagatodo.core.views.resetpassword.ResetPasswordViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ResetPasswordModule::class))
interface ResetPasswordComponent {
    fun inject(resetPasswordInteractor: ResetPasswordInteractor)
    fun inject(resetPasswordRepository: ResetPasswordRepository)
    fun inject(resetPasswordViewModel: ResetPasswordViewModel)
}