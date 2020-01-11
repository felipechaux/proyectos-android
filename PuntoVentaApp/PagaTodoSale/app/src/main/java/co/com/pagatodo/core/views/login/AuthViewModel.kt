package co.com.pagatodo.core.views.login

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.interactors.IAuthInteractor
import co.com.pagatodo.core.data.interactors.ILocalInteractor
import co.com.pagatodo.core.data.model.PrinterConfigurationModel
import co.com.pagatodo.core.di.AuthModule
import co.com.pagatodo.core.di.DaggerAuthComponent
import co.com.pagatodo.core.di.LocalModule
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.print.tmu.EpsonDevice
import co.com.pagatodo.core.views.SingleLiveEvent
import java.net.ConnectException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthViewModel : ViewModel() {

    sealed class ViewEvent {
        class AuthError(val errorMessage: String) : ViewEvent()
        class AuthSuccessWithSellerCode(val sellerCode: String) : ViewEvent()
        object AuthSuccess : ViewEvent()
        class AuthFingerPrintSellerSuccess() : ViewEvent()
        class AuthFingerPrintSellerError(val errorMessage: String) : ViewEvent()
        class AuthTokenSuccess() : ViewEvent()
        class AuthTokenError(val errorMessage: String) : ViewEvent()
    }

    @Inject
    lateinit var authInteractor: IAuthInteractor
    @Inject
    lateinit var localInteractor: ILocalInteractor

    var singleLiveEvent: SingleLiveEvent<ViewEvent> =
            SingleLiveEvent()

    init {
        DaggerAuthComponent.builder()
                .authModule(AuthModule())
                .localModule(LocalModule())
                .build().inject(this)
    }

    @SuppressLint("CheckResult")
    fun auth(username: String, password: String) {
        authInteractor.auth(username, password)?.subscribe({
            if (it.isSuccess && it.responseCode == R_string(R.string.response_success_code)) {
                authInteractor.getGeneralParameters()
                singleLiveEvent.value = ViewEvent.AuthSuccessWithSellerCode(username)
            } else {
                val message = it.message
                singleLiveEvent.value = ViewEvent.AuthError(
                        message ?: R_string(R.string.text_error_login)
                )
            }
        }, {
            if (it is ConnectException) {
                singleLiveEvent.value = ViewEvent.AuthError(R_string(R.string.message_no_network))
            } else {
                singleLiveEvent.value = ViewEvent.AuthError(
                        R_string(R.string.text_error_request)
                )
            }
        })
    }

    fun saveDefaultEpsonConfiguration(epsonConfiguration: EpsonDevice) {
        val pConfig = PrinterConfigurationModel().apply {
            printerType = "01"
            pathConnection = epsonConfiguration.target
        }
        localInteractor.savePrinterConfiguration(pConfig)
    }

    @SuppressLint("CheckResult")
    fun authFingerPrintSeller(identificationType: String, identificationNumber: String, fingerPrint: String) {

        authInteractor.authFingerPrintSeller(
                identificationType,
                identificationNumber,
                fingerPrint
        )?.subscribe({

            if (it.isSuccess ?: false && it.responseCode == R_string(R.string.response_code_zero_2) ) {
                singleLiveEvent.value = ViewEvent.AuthFingerPrintSellerSuccess()
            } else {
                singleLiveEvent.value = ViewEvent.AuthFingerPrintSellerError(it.message ?: "")
            }

        }, {
            singleLiveEvent.value = ViewEvent.AuthError(RUtil.R_string(R.string.message_no_network))
        })


    }

    @SuppressLint("CheckResult")
    fun authToken(identificationNumber: String, token: String) {

        authInteractor.authToken(
                identificationNumber,
                token
        )?.subscribe({

            if (it.isSuccess ?: false && it.responseCode == R_string(R.string.response_code_zero_2)) {
                singleLiveEvent.value = ViewEvent.AuthTokenSuccess()
            } else {
                singleLiveEvent.value = ViewEvent.AuthTokenError(it.message ?: "")
            }

        }, {
            singleLiveEvent.value = ViewEvent.AuthError(RUtil.R_string(R.string.message_no_network))
        })


    }

}