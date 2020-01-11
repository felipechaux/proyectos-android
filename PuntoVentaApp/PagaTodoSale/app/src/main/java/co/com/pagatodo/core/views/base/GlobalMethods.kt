package co.com.pagatodo.core.views.base

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.LastTransactionStatusModel
import co.com.pagatodo.core.network.retryCount
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.util.lastTransactionStatusObject
import com.google.gson.Gson
import java.lang.IllegalStateException
import java.net.ConnectException
import java.net.SocketTimeoutException

fun saveLastTransaction(request: Any, productName: String) {
    retryCount = 0
    val lastTransaction = LastTransactionStatusModel().apply {
        this.sellerCode = SharedPreferencesUtil.getPreference(R_string(R.string.shared_key_user_id))
        this.productName = productName
        this.request = Gson().toJson(request)
    }
    StorageUtil.saveLastTransactionStatus(lastTransaction)
    lastTransactionStatusObject = request
}

fun removeLastTransaction() {
    StorageUtil.removeLastTransaction()
}

fun removeLastTransaction(error: Throwable){
    if (error !is SocketTimeoutException && error !is IllegalStateException && error !is ConnectException){
        StorageUtil.removeLastTransaction()
        BaseObservableViewModel.baseSubject.onNext(BaseEvents.HideProgressDialog)
    }
    else if (error is SocketTimeoutException) {
        if (retryCount > StorageUtil.getRetryCount().toInt()) {
            BaseObservableViewModel.baseSubject.onNext(BaseEvents.HideProgressDialog)
        }
    }
}