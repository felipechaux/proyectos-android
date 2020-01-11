package co.com.pagatodo.core.views.resetpassword

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.interactors.ResetPasswordInteractor
import co.com.pagatodo.core.data.model.request.RequestResetPasswordModel
import co.com.pagatodo.core.di.DaggerResetPasswordComponent
import co.com.pagatodo.core.di.ResetPasswordModule
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.SingleLiveEvent
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResetPasswordViewModel: ViewModel() {

    sealed class ViewEvent {
        class ResponseError(val errorMessage: String?): ViewEvent()
        class ResponseSuccess(val successMessage: String?): ViewEvent()
    }

    @Inject
    lateinit var resetPasswordInteractor: ResetPasswordInteractor

    var singleLiveEvent: SingleLiveEvent<ViewEvent> =
        SingleLiveEvent()

    init {
        DaggerResetPasswordComponent.builder().resetPasswordModule(ResetPasswordModule()).build().inject(this)
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        resetPassword(currentPassword, newPassword)
    }
}

/*Extensions*/
@SuppressLint("CheckResult")
private fun ResetPasswordViewModel.resetPassword(currentPassword: String, newPassword: String) {
    resetPasswordInteractor.resetPassword(currentPassword, newPassword)?.subscribe ({
        if(it.success) {
            singleLiveEvent.value =
                ResetPasswordViewModel.ViewEvent.ResponseSuccess(it.message)
        } else {
            singleLiveEvent.value =
                ResetPasswordViewModel.ViewEvent.ResponseError(it.message)
        }
    },{
        if (it is ConnectException){
            singleLiveEvent.value = ResetPasswordViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
        }
        else if (it !is SocketTimeoutException) {
            singleLiveEvent.value =
                    ResetPasswordViewModel.ViewEvent.ResponseError( R_string(R.string.message_error_transaction))
        }
    })
}