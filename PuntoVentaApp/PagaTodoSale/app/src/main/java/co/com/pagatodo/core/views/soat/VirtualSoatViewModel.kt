package co.com.pagatodo.core.views.soat

import android.annotation.SuppressLint
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.InputDataDTO
import co.com.pagatodo.core.data.dto.request.RequestIssuePolicyDTO
import co.com.pagatodo.core.data.dto.request.RequestPolicyConfirmDTO
import co.com.pagatodo.core.data.dto.response.ResponseIssuePolicyDTO
import co.com.pagatodo.core.data.dto.response.ResponseQueryVirtualSoatDTO
import co.com.pagatodo.core.data.interactors.ILocalInteractor
import co.com.pagatodo.core.data.interactors.IVirtualSoatInteractor
import co.com.pagatodo.core.data.model.print.VirtualSoatPrintModel
import co.com.pagatodo.core.data.print.IPrinterStatusInfo
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.DaggerVirtualSoatComponent
import co.com.pagatodo.core.di.LocalModule
import co.com.pagatodo.core.di.VirtualSoatModule
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.SingleLiveEvent
import co.com.pagatodo.core.views.base.BaseEvents
import co.com.pagatodo.core.views.base.BaseObservableViewModel
import co.com.pagatodo.core.views.base.BaseViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VirtualSoatViewModel : BaseViewModel() {

    @Inject
    internal lateinit var virtualSoatInteractor: IVirtualSoatInteractor

    @Inject
    lateinit var localInteractor: ILocalInteractor

    @Inject
    lateinit var printerStatusInfo: IPrinterStatusInfo

    lateinit var mHeader1: String
    lateinit var mHeader2: String
    lateinit var mFooter: String

    sealed class ViewEvent {
        class Errors(val errorMessage: String) : ViewEvent()
        class GetQueryVirtualSoatSuccess(val responseSoatQuery: ResponseQueryVirtualSoatDTO) : ViewEvent()
        class LoadParameters(val documents: List<String>, val vehicles: List<String>) : ViewEvent()

    }

    var singleLiveEvent: SingleLiveEvent<ViewEvent> = SingleLiveEvent()

    init {

        DaggerVirtualSoatComponent
            .builder()
            .virtualSoatModule(VirtualSoatModule())
            .localModule(LocalModule())
            .build()
            .inject(this)
    }

    //"soatvirtual/consultarSoatVirtual"
    @SuppressLint("CheckResult")
    fun getQueryVirtualSoat(inputDataDTO: InputDataDTO) {
        virtualSoatInteractor.getQueryVirtualSoat(inputDataDTO)?.subscribe({

            if (it.isSuccess ?: false) {
                singleLiveEvent.value = ViewEvent.GetQueryVirtualSoatSuccess(it)
            } else {
                singleLiveEvent.value = ViewEvent.Errors(it.message ?: "")
            }

        }, {
            singleLiveEvent.value = ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
        })
    }

    // soatvirtual/emitirPoliza
    @SuppressLint("CheckResult")
    fun getIssuePolicy(requestIssuePolicyDTO: RequestIssuePolicyDTO) {

        validatePrinterStatus(printerStatusInfo) {

            virtualSoatInteractor.getIssuePolicy(requestIssuePolicyDTO)?.subscribe({

                if (it.isSuccess ?: false) {

                    val requestPolicyConfirmDTO = RequestPolicyConfirmDTO().apply {

                        this.plaque = requestIssuePolicyDTO.licensePlate
                        this.productTransactionId = it.issuePolicy?.responseCode
                        this.collectionValue = requestIssuePolicyDTO.collectingValue
                    }

                    policyConfirm(it, requestPolicyConfirmDTO)
                    //singleLiveEvent.value = ViewEvent.GetIssuePolicySuccess(it)

                } else {
                    singleLiveEvent.value = ViewEvent.Errors(it.message ?: "")
                }

            }, {
                singleLiveEvent.value =
                    ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
            })

        }
    }

    @SuppressLint("CheckResult")
    private fun policyConfirm(responseIssuePolicyDTO:ResponseIssuePolicyDTO,requestPolicyConfirmDTO: RequestPolicyConfirmDTO) {

        virtualSoatInteractor.policyConfirm(requestPolicyConfirmDTO)?.subscribe({

            if (it.isSuccess ?: false) {

                printSoat(responseIssuePolicyDTO)

            } else {

                singleLiveEvent.value = ViewEvent.Errors(it.message ?: "")
            }

        }, {
            singleLiveEvent.value = ViewEvent.Errors(RUtil.R_string(R.string.message_no_network))
        })


    }


    @SuppressLint("CheckResult")
    fun getParameter() {

        localInteractor.getProductInfo(RUtil.R_string(R.string.code_product_virtual_soat)).subscribe {


            mHeader1 = it.parameters?.filter { it.key == RUtil.R_string(R.string.soat_parametrer_header1) }?.map { it.value }?.last()?:""
            mHeader2 = it.parameters?.filter { it.key == RUtil.R_string(R.string.soat_parametrer_header2) }?.map { it.value }?.last()?:""
            mFooter = it.parameters?.filter { it.key == RUtil.R_string(R.string.soat_parametrer_footer) }?.map { it.value }?.last()?:""

            singleLiveEvent.value = ViewEvent.LoadParameters(
                it.parameters?.filter { it.key == RUtil.R_string(R.string.soat_parametrer_id_type) }?.map { it.value }?.last()?.split(
                    ","
                )!!,
                it.parameters?.filter { it.key == RUtil.R_string(R.string.soat_parametrer_vehicle_type) }?.map { it.value }?.last()?.split(
                    ","
                )!!
            )

        }

    }


    fun printSoat(response: ResponseIssuePolicyDTO) {


       val  virtualSoatPrintModel = VirtualSoatPrintModel().apply{
           this.header = "$mHeader1\\n$mHeader2\\n"
           this.footer = mFooter
           this.transactionDate = response.transactionDate
           this.transactionHour = response.transactionHour
           this.beginValidityDateHour = response.beginValidityDateHour
           this.endValidityDateHour = response.ensValidityDateHour
           this.policyNumber = response.policyNumber
           this.soatValue = response.soatValue
           this.fosygaValue = response.fosygaValue
           this.runtValue = response.runtValue
           this.totalValue = response.toalValue
           this.takerNameLast = response.takerNameLast
           this.documentType = "CC"
           this.takerDocumentNumber = response.takerDocumentNumber
           this.takerCity = response.takerCity
           this.vehicleType = response.vehicleType
           this.service = response.service
           this.cylinderCapacity = response.cylinderCapacity
           this.vehicleModel = response.vehicleModel
           this.licensePlate = response.licensePlate
           this.vehicleLine = response.vehicleLine
           this.motorNumber = response.motorNumber
           this.chassisNumber = response.chassisNumber
           this.vinNumber = response.vinNumber
           this.passengers = response.passengers
           this.capacity = response.capacity
           this.transactionQuantity = response.transactionId

       }

        virtualSoatInteractor.printSoat(virtualSoatPrintModel){

            if (it == PrinterStatus.OK)
                BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("",RUtil.R_string(R.string.bbva_deposits_transaction_success_title),true))
            else
                BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", R_string(R.string.message_error_print_device)))

        }
    }


}