package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.dto.request.RequestResetPasswordDTO
import co.com.pagatodo.core.data.dto.response.ResponseResetPasswordDTO
import co.com.pagatodo.core.di.DaggerResetPasswordComponent
import co.com.pagatodo.core.di.ResetPasswordModule
import co.com.pagatodo.core.network.ApiFactory
import io.reactivex.Observable
import javax.inject.Singleton

@Singleton
class ResetPasswordRepository: BaseRepository(),
    IResetPasswordRepository {

    init {
        DaggerResetPasswordComponent.builder().resetPasswordModule(ResetPasswordModule()).build().inject(this)
    }

    override fun resetPassword(requestResetPasswordDTO: RequestResetPasswordDTO): Observable<ResponseResetPasswordDTO>? {
        return ApiFactory.build()?.resetPassword(requestResetPasswordDTO)
    }
}