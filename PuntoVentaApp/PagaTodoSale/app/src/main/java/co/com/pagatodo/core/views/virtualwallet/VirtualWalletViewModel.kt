package co.com.pagatodo.core.views.virtualwallet

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.com.pagatodo.core.PagaTodoApplication.Companion.getAppContext
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.interactors.IVitualWalletInteractor
import co.com.pagatodo.core.data.model.print.VirtualWalletPrintModel
import co.com.pagatodo.core.data.model.request.RequestActivatePinModel
import co.com.pagatodo.core.data.model.response.ResponseActivatePinModel
import co.com.pagatodo.core.data.model.response.ResponseVirtualWalletQueryPinModel
import co.com.pagatodo.core.data.print.IPrinterStatusInfo
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.DaggerVirtualWalletComponent
import co.com.pagatodo.core.di.VirtualWalletModule
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.views.SingleLiveEvent
import co.com.pagatodo.core.views.base.BaseEvents
import co.com.pagatodo.core.views.base.BaseObservableViewModel
import co.com.pagatodo.core.views.base.BaseViewModel
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VirtualWalletViewModel : BaseViewModel() {

    @Inject
    lateinit var interactor: IVitualWalletInteractor

    @Inject
    lateinit var printerStatusInfo: IPrinterStatusInfo

    sealed class ViewEvent {
        class ResponseError(val errorMessage: String?) : ViewEvent()
        class ResponseSuccess(val successMessage: String?) : ViewEvent()
    }

    var singleLiveEvent: SingleLiveEvent<ViewEvent> = SingleLiveEvent()
    lateinit var currentPin: MutableLiveData<ResponseVirtualWalletQueryPinModel>
    var textHeaderProduct = ""
    var textBodyProduct = ""

    init {
        DaggerVirtualWalletComponent
            .builder().virtualWalletModule(VirtualWalletModule())
            .build().inject(this)

        if (!::currentPin.isInitialized) {
            currentPin = MutableLiveData()
        }
    }

    fun dispatchQueryPin(pin: String) {
        queryPin(pin)
    }

    fun dispatchActivatePin(
        request: RequestActivatePinModel,
        isRetry: Boolean = true,
        transactionType: String? = null
    ) {
        activatePin(request, isRetry, transactionType)
    }

    fun dispatchGetProductParameters() {
        getProductParameters()
    }
}

@SuppressLint("CheckResult")
private fun VirtualWalletViewModel.getProductParameters() {
    interactor.getProductParameters()?.subscribe({
        it.forEach {
            if (it.key.equals(R_string(R.string.name_column_header_recharge))) {
                textHeaderProduct = it.value ?: ""
            }
            if (it.key.equals(R_string(R.string.name_column_body_recharge))) {
                textBodyProduct = it.value ?: ""
            }
        }
    }, {

    })
}

@SuppressLint("CheckResult")
private fun VirtualWalletViewModel.queryPin(pin: String) {
    interactor.queryPin(pin)?.subscribe({
        if (it.isSuccess == true) {
            currentPin.value = it
        } else {
            singleLiveEvent.value = VirtualWalletViewModel.ViewEvent.ResponseError(
                it.message ?: R_string(R.string.message_error_transaction)
            )
        }
    }, {

        if (it is ConnectException)
            singleLiveEvent.value =
                VirtualWalletViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
        else if (it !is SocketTimeoutException)
            singleLiveEvent.value =
                VirtualWalletViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))



    })
}

@SuppressLint("CheckResult")
private fun VirtualWalletViewModel.activatePin(
    request: RequestActivatePinModel,
    isRetry: Boolean = true,
    transactionType: String? = null
) {
    validatePrinterStatus(printerStatusInfo) {
        interactor.activatePin(request, isRetry, transactionType)?.subscribe({
            if (it.isSuccess == true) {
                StorageUtil.updateStub(it.serie1 ?: "", it.currentSerie2 ?: "")
                print(it)
            } else {
                singleLiveEvent.value = VirtualWalletViewModel.ViewEvent.ResponseError(
                    it.message ?: R_string(R.string.message_error_transaction)
                )
            }
        }, {
            singleLiveEvent.value =
                VirtualWalletViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
        })
    }
}

private fun VirtualWalletViewModel.print(response: ResponseActivatePinModel) {
    val printModel = VirtualWalletPrintModel().apply {
        textHeader = textHeaderProduct
        textBody = textBodyProduct
        pin = response.pin?.pin ?: ""
        pinValue = response.pin?.value ?: ""
    }

    interactor.print(printModel) { status ->
        if (status == PrinterStatus.OK) {
            BaseObservableViewModel.baseSubject.onNext(
                BaseEvents.ShowAlertDialogInMenu(
                    R_string(R.string.message_success_recharge_virtual_wallet),
                    getAppContext().getString(
                        R.string.text_label_success_recharge_virtual_wallet,
                        currentPin.value?.pin?.pin ?: R_string(R.string.text_success_transaction)
                    ), true
                )
            )
        } else {
            BaseObservableViewModel.baseSubject.onNext(
                BaseEvents.ShowAlertDialogInMenu(
                    "",
                    response.message ?: R_string(R.string.message_error_transaction),
                    false
                )
            )
        }
    }
}