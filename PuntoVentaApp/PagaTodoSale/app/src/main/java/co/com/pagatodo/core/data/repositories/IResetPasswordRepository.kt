package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.dto.request.RequestResetPasswordDTO
import co.com.pagatodo.core.data.dto.response.ResponseResetPasswordDTO
import io.reactivex.Observable

interface IResetPasswordRepository {

    fun resetPassword(requestResetPasswordDTO: RequestResetPasswordDTO): Observable<ResponseResetPasswordDTO>?
}