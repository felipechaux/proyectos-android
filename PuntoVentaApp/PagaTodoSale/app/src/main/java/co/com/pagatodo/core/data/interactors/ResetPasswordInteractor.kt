package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.request.RequestResetPasswordDTO
import co.com.pagatodo.core.data.model.response.ResponseResetPasswordModel
import co.com.pagatodo.core.data.repositories.IResetPasswordRepository
import co.com.pagatodo.core.di.DaggerResetPasswordComponent
import co.com.pagatodo.core.di.ResetPasswordModule
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SecurityUtil.Companion.convertStringToSH256
import co.com.pagatodo.core.util.SecurityUtil.Companion.encryptTextTo3Des
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResetPasswordInteractor {

    @Inject
    lateinit var resetPasswordRepository: IResetPasswordRepository

    init {
        DaggerResetPasswordComponent.builder().resetPasswordModule(ResetPasswordModule()).build().inject(this)
    }

    fun resetPassword(currentPassword: String, newPassword: String): Observable<ResponseResetPasswordModel>? {
        val requestResetPasswordDTO = createRequestResetPasswordDTO(currentPassword, newPassword)
        return resetPasswordRepository
            .resetPassword(requestResetPasswordDTO)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val model = ResponseResetPasswordModel().apply {
                    responseCode = it.responseCode
                    success = it.success
                    transactionDate = it.transactionDate
                    transactionTime = it.transactionTime
                    message = it.message
                }
                Observable.just(model)
            }
    }

    private fun createRequestResetPasswordDTO(currentPassword: String, newPassword: String): RequestResetPasswordDTO {
        val encryptedCurrentPassword = convertStringToSH256(currentPassword)

        val simpleDateFormat = SimpleDateFormat(DateUtil.StringFormat.DDDYYYY.rawValue)
        val currenDate = simpleDateFormat.format(Date())
        val encryptionKey = getPreference<String>(R_string(R.string.seed_param_service)) + currenDate

        val encryptedNewPassword = encryptTextTo3Des(newPassword, encryptionKey)
        return RequestResetPasswordDTO().apply {
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            userType = getPreference(R_string(R.string.shared_key_user_type))
            this.currentPassword = encryptedCurrentPassword
            this.newPassword = encryptedNewPassword
        }
    }
}