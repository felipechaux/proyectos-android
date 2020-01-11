package co.com.pagatodo.core.views.base

import androidx.fragment.app.Fragment
import io.reactivex.subjects.PublishSubject


enum class NetworkEvents {
    SocketTimeoutException,
    UnauthorizedService,
    updateViewStub,
    MaxNumberRetry
}

sealed class BaseEvents {
    object HideProgressDialog: BaseEvents()
    class ShowAlertDialogInMenu(val title: String, val message: String, val isSuccessTransaction: Boolean = false,val fragment: BaseFragment? = null): BaseEvents()
    class ShowAlertDialogSession(val title: String, val message: String): BaseEvents()
    class ShowAlertDialogClosure(val title: String, val message: String,val fragment: BaseFragment? = null,val closure: () -> Unit?): BaseEvents()
}

class BaseObservableViewModel {

    companion object {
        val networkSubject = PublishSubject.create<NetworkEvents>()
        val baseSubject = PublishSubject.create<BaseEvents>()
    }
}