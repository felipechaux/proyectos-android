package co.com.pagatodo.core.views.wallet

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.interactors.WalletInteractor
import co.com.pagatodo.core.data.model.KeyValueModel
import co.com.pagatodo.core.data.model.ModalityModel
import co.com.pagatodo.core.data.model.request.RequestWalletModel
import co.com.pagatodo.core.data.model.response.ResponseWalletModel
import co.com.pagatodo.core.di.DaggerWalletComponent
import co.com.pagatodo.core.di.WalletModule
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.SingleLiveEvent
import co.com.pagatodo.core.views.superchance.SuperChanceViewModel
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject

class WalletViewModel: ViewModel() {
    @Inject
    lateinit var walletInteractor: WalletInteractor
    lateinit var walletLiveData: MutableLiveData<ResponseWalletModel>

    sealed class ViewEvent {
        class ResponseError(val errorMessage: String?): ViewEvent()
        class ResponseSuccess(val successMessage: String?): ViewEvent()
    }

    var singleLiveEvent: SingleLiveEvent<ViewEvent> = SingleLiveEvent()

    init {
        DaggerWalletComponent.builder().walletModule(WalletModule()).build().inject(this)

        if (!::walletLiveData.isInitialized) {
            walletLiveData = MutableLiveData()
        }
    }
    fun fetchWallet(dateQuery: String, sellerCode: String){
        getWallet(dateQuery, sellerCode)
    }
}

@SuppressLint("CheckResult")
private fun WalletViewModel.getWallet(dateQuery: String, sellerCode: String){
    walletInteractor.getWallet(dateQuery, sellerCode)?.subscribe ({
        walletLiveData.value = it
    },{
        if (it is ConnectException){
            singleLiveEvent.value = WalletViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
        } else if (it !is SocketTimeoutException) {
            val message = it.message
            singleLiveEvent.value = WalletViewModel.ViewEvent.ResponseError(message)
        }
    })
}