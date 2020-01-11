package co.com.pagatodo.core.views.recharge

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.interactors.ILocalInteractor
import co.com.pagatodo.core.data.interactors.IUtilInteractor
import co.com.pagatodo.core.data.interactors.RechargeInteractor
import co.com.pagatodo.core.data.model.KeyValueModel
import co.com.pagatodo.core.data.model.OperatorModel
import co.com.pagatodo.core.data.model.ProductName
import co.com.pagatodo.core.data.model.RequestRechargeModel
import co.com.pagatodo.core.data.model.request.RequestRechargeHistoryModel
import co.com.pagatodo.core.data.model.request.RequestUtilModel
import co.com.pagatodo.core.data.model.response.ResponseRechargeHistoryModel
import co.com.pagatodo.core.data.model.response.ResponseRechargeModel
import co.com.pagatodo.core.data.print.IPrinterStatusInfo
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.DaggerRechargeComponent
import co.com.pagatodo.core.di.LocalModule
import co.com.pagatodo.core.di.PrinterStatusModule
import co.com.pagatodo.core.di.RechargeModule
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.views.SingleLiveEvent
import co.com.pagatodo.core.views.base.BaseEvents
import co.com.pagatodo.core.views.base.BaseObservableViewModel
import co.com.pagatodo.core.views.base.removeLastTransaction
import co.com.pagatodo.core.views.base.saveLastTransaction
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RechargeViewModel : ViewModel() {

    sealed class ViewEvent {
        class ResponseError(val errorMessage: String?) : ViewEvent()
        class ResponseSuccess(val successMessage: String?) : ViewEvent()
        class ResponseOperators(val operators: List<OperatorModel>) : ViewEvent()
        class ResponseProductParameters(val productParameters: List<KeyValueModel>) : ViewEvent()
        class ResponseRechargeHistory(val rechargeHistoryModel: ResponseRechargeHistoryModel) :
            ViewEvent()

        class RechargeSuccess(val responseRechargeModel: ResponseRechargeModel) : ViewEvent()
    }

    @Inject
    lateinit var rechargeInteractor: RechargeInteractor
    @Inject
    lateinit var printerStatusInfo: IPrinterStatusInfo
    @Inject
    lateinit var localInteractor: ILocalInteractor
    @Inject
    internal lateinit var utilInteractor: IUtilInteractor

    var singleLiveEvent: SingleLiveEvent<ViewEvent> =
        SingleLiveEvent()

    init {
        DaggerRechargeComponent
            .builder()
            .rechargeModule(RechargeModule())
            .printerStatusModule(PrinterStatusModule())
            .localModule(LocalModule())
            .build().inject(this)
    }

    fun loadOperators() {
        getOperators()
    }

    fun getProductParameters() {
        loadProductParameters()
    }

    fun checkRecharge(requestRechargeHistoryModel: RequestRechargeHistoryModel) {
        getRechargeHistory(requestRechargeHistoryModel)
    }

    @SuppressLint("CheckResult")
    fun updateStubInServer(responseRechargeModel: ResponseRechargeModel, rechargeAmount: String) {

        val requestModel = RequestUtilModel().apply {
            serie1 = SharedPreferencesUtil.getPreference(R_string(R.string.shared_key_current_serie1))
            serie2 = SharedPreferencesUtil.getPreference(R_string(R.string.shared_key_current_serie2))
            operationCode = R_string(R.string.move_stub)
            sellerCode = SharedPreferencesUtil.getPreference(R_string(R.string.shared_key_seller_code))
        }

        utilInteractor.updateStubInServer(requestModel)?.subscribe({

            if(it.isSuccess?:false){

                SharedPreferencesUtil.savePreference(R_string(R.string.shared_key_current_serie2), it?.values?.get(0)?.value.toString())
                dispatchPrintRecharge(responseRechargeModel,rechargeAmount)

            }else{
                singleLiveEvent.value = RechargeViewModel.ViewEvent.ResponseError(it.message)
                BaseObservableViewModel.baseSubject.onNext(
                    BaseEvents.ShowAlertDialogInMenu(
                        "",
                        R_string(R.string.message_error_transaction)
                    )
                )
            }


        }, {
            singleLiveEvent.value = RechargeViewModel.ViewEvent.ResponseError(it.message)
            BaseObservableViewModel.baseSubject.onNext(
                BaseEvents.ShowAlertDialogInMenu(
                    "",
                    R_string(R.string.message_error_transaction)
                )
            )
        })
    }

    fun printRecharge(responseRechargeModel: ResponseRechargeModel, rechargeAmount: String) {

        if(DeviceUtil.isSalePoint())
            dispatchPrintRecharge(responseRechargeModel, rechargeAmount)
        else
            updateStubInServer(responseRechargeModel, rechargeAmount)
    }

    fun dispatchPrintRecharge(responseRechargeModel: ResponseRechargeModel, rechargeAmount: String) {

        val stub =
            "    ${responseRechargeModel.recharge?.serie1}    ${responseRechargeModel.recharge?.serie2}"
        val billNumber = responseRechargeModel.recharge?.currentBill ?: ""
        val transactionDate = DateUtil.convertStringToDate(
            DateUtil.StringFormat.MMM_DD_YY,
            DateUtil.addBackslashToStringDate(responseRechargeModel.recharge?.dateRecharge ?: "")
        ).toLowerCase()
        val transactionHour = responseRechargeModel.recharge?.hourRecharge ?: ""
        val number = responseRechargeModel.recharge?.number ?: ""
        val value = rechargeAmount ?: ""
        val billPrefix = responseRechargeModel.recharge?.billPrefix ?: ""
        val billMessage = responseRechargeModel.recharge?.billMessage ?: ""
        val headerGelsa =
            localInteractor.getStringValueParamFromLocal(R_string(R.string.param_service_gelsa_header))
                ?: ""
        val decriptionPackage = responseRechargeModel.decriptionPackage

        rechargeInteractor.printRecharge(
            stub,
            billNumber,
            transactionDate,
            transactionHour,
            number,
            value,
            billPrefix,
            billMessage,
            false,
            headerGelsa,
            decriptionPackage?:"",
            responseRechargeModel.operatorName?:""
        ) {
            if (it == PrinterStatus.OK) {
                BaseObservableViewModel.baseSubject.onNext(
                    BaseEvents.ShowAlertDialogInMenu(
                        "",
                        R_string(R.string.message_success_recharge_print_success),
                        true
                    )
                )
            } else {
                singleLiveEvent.value =
                    RechargeViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_print_device))
                BaseObservableViewModel.baseSubject.onNext(
                    BaseEvents.ShowAlertDialogInMenu(
                        "",
                        R_string(R.string.message_error_print_device)
                    )
                )
            }
        }

    }

}

/*Extensions*/
@SuppressLint("CheckResult")
private fun RechargeViewModel.getOperators() {
    rechargeInteractor.getOperatorsForRechargeRoom()?.subscribe {
        if (it.isNotEmpty()) {
            singleLiveEvent.value = RechargeViewModel.ViewEvent.ResponseOperators(it)
        }
    }
}

@SuppressLint("CheckResult")
private fun RechargeViewModel.loadProductParameters() {

    localInteractor.getProductInfo(R_string(R.string.code_recharge)).subscribe {

        singleLiveEvent.value =
            RechargeViewModel.ViewEvent.ResponseProductParameters(it.parameters!!)


    }

}


@SuppressLint("CheckResult")
fun RechargeViewModel.dispatchRecharge(
    requestRechargeModel: RequestRechargeModel,
    isRetry: Boolean = true,
    transactionType: String? = null
) {
    if (transactionType == null)
        saveLastTransaction(requestRechargeModel, ProductName.RECHARGE.rawValue)

    rechargeInteractor.dispatchRecharge(requestRechargeModel, isRetry, transactionType)
        ?.subscribe({


            if (it.success == true) {

                it.recharge?.serie1 = SharedPreferencesUtil.getPreference(R_string(R.string.shared_key_current_serie1))
                it.recharge?.serie2 = SharedPreferencesUtil.getPreference(R_string(R.string.shared_key_current_serie2))

                if (it.serie1 != null && it.currentSerie2 != null)
                    StorageUtil.updateStub(it.serie1 ?: "", it.currentSerie2 ?: "")

                it.decriptionPackage = requestRechargeModel.descroptionPackage
                it.operatorName = requestRechargeModel.operatorName

                removeLastTransaction()
                singleLiveEvent.value = RechargeViewModel.ViewEvent.RechargeSuccess(it)


            } else {
                removeLastTransaction()
                singleLiveEvent.value = RechargeViewModel.ViewEvent.ResponseError(it.message)
                BaseObservableViewModel.baseSubject.onNext(
                    BaseEvents.ShowAlertDialogInMenu(
                        "",
                        R_string(R.string.message_error_transaction)
                    )
                )
            }
        }, {
            removeLastTransaction(it)
            if (it is HttpException || it !is SocketTimeoutException) {
                singleLiveEvent.value =
                    RechargeViewModel.ViewEvent.ResponseError( R_string(R.string.message_error_transaction))
            } else if (it is ConnectException) {
                singleLiveEvent.value =
                    RechargeViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
            }
        })
}

@SuppressLint("CheckResult")
private fun RechargeViewModel.getRechargeHistory(requestRechargeHistoryModel: RequestRechargeHistoryModel) {
    rechargeInteractor.getRechargeHistory(requestRechargeHistoryModel)?.subscribe({
        singleLiveEvent.value =
            RechargeViewModel.ViewEvent.ResponseRechargeHistory(it)
    }, {
        if (it is ConnectException) {
            singleLiveEvent.value =
                RechargeViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
        } else {
            singleLiveEvent.value =
                RechargeViewModel.ViewEvent.ResponseError( R_string(R.string.message_error_transaction))
        }
    })
}