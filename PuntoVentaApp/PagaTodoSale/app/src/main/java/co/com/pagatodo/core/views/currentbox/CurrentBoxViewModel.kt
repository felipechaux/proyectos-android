package co.com.pagatodo.core.views.currentbox

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.PagaTodoApplication.Companion.printerDevice
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.interactors.CurrentBoxInteractor
import co.com.pagatodo.core.data.interactors.IUtilInteractor
import co.com.pagatodo.core.data.model.ErrorResponseModel
import co.com.pagatodo.core.data.model.request.RequestUtilModel
import co.com.pagatodo.core.data.model.response.ResponseCurrentBoxModel
import co.com.pagatodo.core.data.print.IPrinterStatusInfo
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.CurrentBoxModule
import co.com.pagatodo.core.di.DaggerCurrentBoxComponent
import co.com.pagatodo.core.di.UtilModule
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.savePreference
import co.com.pagatodo.core.views.base.BaseEvents
import co.com.pagatodo.core.views.base.BaseObservableViewModel
import co.com.pagatodo.core.views.base.BaseViewModel
import com.cloudpos.POSTerminal
import com.cloudpos.printer.PrinterDevice
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentBoxViewModel :BaseViewModel() {

    @Inject
    lateinit var currentBoxInteractor: CurrentBoxInteractor
    @Inject
    internal lateinit var utilInteractor: IUtilInteractor
    @Inject
    lateinit var printerStatusInfo: IPrinterStatusInfo

    var rechargeLiveDataCurrentBoxSuccess = MutableLiveData<ResponseCurrentBoxModel>()
    var rechargeLiveDataErrorResponse = MutableLiveData<ErrorResponseModel>()

    init {
        DaggerCurrentBoxComponent
            .builder()
            .currentBoxModule(CurrentBoxModule())
            .utilModule(UtilModule())
            .build().inject(this)
    }

    fun getCurrentBox(currentDate: String? = null) {
        checkCurrentBox(currentDate)
    }

    fun launchUpdateStub() {

        //validatePrinterStatus(printerStatusInfo) {
            val requestModel = RequestUtilModel().apply {
                serie1 = getPreference(R_string(R.string.shared_key_current_serie1))
                serie2 = getPreference(R_string(R.string.shared_key_current_serie2))
                operationCode = R_string(R.string.move_stub)
                sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            }
            updateStubInServer(requestModel)
        //}
    }

    fun printCurrentBox(responseModel: ResponseCurrentBoxModel, stubs:String){
        print(responseModel,stubs)
    }

}

/*Extensions*/
@SuppressLint("CheckResult")
private fun CurrentBoxViewModel.checkCurrentBox(currentDate: String? = null) {
    currentBoxInteractor.checkCurrentBox(currentDate)?.subscribe({
        rechargeLiveDataCurrentBoxSuccess.value = it
    }, {

        val error = ErrorResponseModel().apply {
            errorCode = it.cause.toString()
            errorMessage = R_string(R.string.message_error_transaction)
        }

        rechargeLiveDataErrorResponse.value = error
    })
}

@SuppressLint("CheckResult")
private fun CurrentBoxViewModel.updateStubInServer(requestModel: RequestUtilModel) {
    utilInteractor.updateStubInServer(requestModel)?.subscribe({
        if (it.isSuccess == true) {

            val stubs = "${requestModel.serie1} ${requestModel.serie2}"

            if(it.values?.get(0)?.value != null)
                savePreference(R_string(R.string.shared_key_current_serie2), it.values?.get(0)?.value.toString())

            print(rechargeLiveDataCurrentBoxSuccess.value ?: ResponseCurrentBoxModel(),stubs)

        }else{

            BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", R_string(R.string.message_error_transaction),true))
        }
    }, {
        val error = ErrorResponseModel().apply {
            errorCode = it.cause.toString()
            errorMessage = R_string(R.string.message_error_not_updated_stubs)
        }

        rechargeLiveDataErrorResponse.value = error
    })
}

private fun CurrentBoxViewModel.print(responseModel: ResponseCurrentBoxModel, stubs:String) {
    currentBoxInteractor.print(responseModel,stubs) {

        if(it == PrinterStatus.OK){
            BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", R_string(R.string.text_print_success_transaccion),true))
        }else{
            BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", R_string(R.string.message_error_print_device),true))
        }

    }
}