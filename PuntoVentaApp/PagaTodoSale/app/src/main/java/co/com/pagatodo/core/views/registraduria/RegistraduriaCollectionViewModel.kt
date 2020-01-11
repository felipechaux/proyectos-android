package co.com.pagatodo.core.views.registraduria

import android.annotation.SuppressLint
import co.com.pagatodo.core.PagaTodoApplication.Companion.getAppContext
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.response.ResponseRegistraduriaCollectionDTO
import co.com.pagatodo.core.data.dto.response.ResponseRegistraduriaGetCollectionDTO
import co.com.pagatodo.core.data.dto.response.ResponseRegistraduriaGetServiceDTO
import co.com.pagatodo.core.data.interactors.IRegistraduriaCollectionInteractor
import co.com.pagatodo.core.data.model.RegistraduriaCollectionModel
import co.com.pagatodo.core.data.model.print.RegistraduriaCollectionPrintModel
import co.com.pagatodo.core.data.print.IPrinterStatusInfo
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.DaggerRegistraduriaCollectionComponent
import co.com.pagatodo.core.di.RegistraduriaCollectionModule
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil
import co.com.pagatodo.core.views.SingleLiveEvent
import co.com.pagatodo.core.views.base.BaseEvents
import co.com.pagatodo.core.views.base.BaseObservableViewModel
import co.com.pagatodo.core.views.base.BaseViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistraduriaCollectionViewModel : BaseViewModel() {

    @Inject
    internal lateinit var registraduriaCollectionInteractor: IRegistraduriaCollectionInteractor

    @Inject
    lateinit var printerStatusInfo: IPrinterStatusInfo

    sealed class ViewEvent {
        class Errors(val errorMessage: String): ViewEvent()
        class GetServiceSuccess(val responseDTO: ResponseRegistraduriaGetServiceDTO): ViewEvent()
    }

    var singleLiveEvent: SingleLiveEvent<ViewEvent> =
        SingleLiveEvent()

    init {
        DaggerRegistraduriaCollectionComponent
            .builder()
            .registraduriaCollectionModule(RegistraduriaCollectionModule())
            .build()
            .inject(this)
    }

    @SuppressLint("CheckResult")
    fun queryRegistraduriaGetCollection(pinNumber: String?, isReprint: Boolean?) {
        validatePrinterStatus(printerStatusInfo) {
            registraduriaCollectionInteractor.queryRegistraduriaGetCollection(pinNumber)?.subscribe({

                if (it.isSuccess!!) {
                    registraduriaCollectionRePrint(it, pinNumber)
                } else {
                    singleLiveEvent.value = ViewEvent.Errors(R_string(R.string.message_no_network))
                }

            }, {
                singleLiveEvent.value = ViewEvent.Errors(R_string(R.string.message_no_network))
            })
        }
    }
    @SuppressLint("CheckResult")
    fun queryRegistraduriaGetServices() {
        registraduriaCollectionInteractor.queryRegistraduriaGetServices()?.subscribe({

            if (it.isSuccess!!) {
                singleLiveEvent.value = ViewEvent.GetServiceSuccess(it)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(R_string(R.string.message_no_network))
            }

        }, {
            singleLiveEvent.value = ViewEvent.Errors(R_string(R.string.message_no_network))
        })
    }


    @SuppressLint("CheckResult")
    fun queryRegistraduriaCollection(registraduriaModel: RegistraduriaCollectionModel) {
        registraduriaCollectionInteractor.queryRegistraduriaCollection(registraduriaModel)?.subscribe({

            if (it.isSuccess!!) {
                registraduriaCollectionPrint(it)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(R_string(R.string.message_no_network))
            }

        }, {
            singleLiveEvent.value = ViewEvent.Errors(R_string(R.string.message_no_network))
        })
    }

    private fun registraduriaCollectionPrint(response: ResponseRegistraduriaCollectionDTO) {
        val currentBillDate = StringBuilder()

        val footerText = R_string(R.string.registraduria_collection_bill_text_original)

        //currentBillDate.append(registraduriaCollectionModel.transactionProductDate).append(" ").append(registraduriaCollectionModel.transactionProductTime)

        val printModel = RegistraduriaCollectionPrintModel().apply {
            textHeader = response.nameCompany
            nitInfo = response.nitCompany
            office = response.officeCode.toString()
            rentCode = response.rentCode.toString()
            sellerCode = SharedPreferencesUtil.getPreference(R_string(R.string.shared_key_seller_code))
            billTitle = R_string(R.string.registraduria_collection_bill_title)
            currentDate = currentBillDate.toString()
            value = response.value.toString()
            secondTitle = response.serviceMessage
            documentNumber = response.documentNumber.toString()
            pin = response.pin.toString()
            names = response.firstName.toString()
            lastNames = response.firstLastName.toString()
            textFooter = response.pageFooter
            originalBill = footerText
        }

        registraduriaCollectionInteractor.registraduriaCollectionPrint(printModel) { status ->
            if (status == PrinterStatus.OK) {
                BaseObservableViewModel.baseSubject.onNext(
                    BaseEvents.ShowAlertDialogInMenu(
                        R_string(R.string.registraduria_collection_message_success),
                        getAppContext().getString(
                            R.string.registraduria_collection_message_success_body,
                            R_string(R.string.text_success_transaction)
                        ), true
                    )
                )
            } else {
                BaseObservableViewModel.baseSubject.onNext(
                    BaseEvents.ShowAlertDialogInMenu(
                        "",
                        R_string(R.string.message_error_transaction),//response.message ?: R_string(R.string.message_error_transaction),
                        false
                    )
                )
            }
        }
    }


    private fun registraduriaCollectionRePrint(response: ResponseRegistraduriaGetCollectionDTO, pinNumber: String?) {
        val currentBillDate = StringBuilder()
        val  footerText = R_string(R.string.registraduria_collection_bill_text_reprint)

        currentBillDate.append(response.transactionProductDate).append(" ").append(response.transactionProductTime)
        val printModel = RegistraduriaCollectionPrintModel().apply {
            textHeader = response.nameCompany
            nitInfo = response.nitCompany
            office = response.officeCode
            rentCode = ""
            sellerCode = SharedPreferencesUtil.getPreference(R_string(R.string.shared_key_seller_code))
            billTitle = R_string(R.string.registraduria_collection_bill_title)
            currentDate = currentBillDate.toString()
            value = response.value.toString()
            secondTitle = response.serviceMessage
            documentNumber = response.documentNumber.toString()
            pin = pinNumber.toString()
            names = response.firstName.toString()
            lastNames = response.firstLastName.toString()
            textFooter = response.pageFooter
            originalBill = footerText
        }

        registraduriaCollectionInteractor.registraduriaCollectionPrint(printModel) { status ->
            if (status == PrinterStatus.OK) {
                BaseObservableViewModel.baseSubject.onNext(
                    BaseEvents.ShowAlertDialogInMenu(
                        R_string(R.string.registraduria_collection_message_success),
                        getAppContext().getString(
                            R.string.registraduria_collection_message_success_body,
                            pinNumber.toString()
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


}