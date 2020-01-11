package co.com.pagatodo.core.views.paybills

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.interactors.IPayBillInteractor
import co.com.pagatodo.core.data.model.AgreementModel
import co.com.pagatodo.core.data.model.BillModel
import co.com.pagatodo.core.data.model.DescriptionAgreementModel
import co.com.pagatodo.core.data.model.print.PayBillPrintModel
import co.com.pagatodo.core.data.model.request.PayBillRequestBillModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.DaggerPayBillComponent
import co.com.pagatodo.core.di.PayBillModule
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.SingleLiveEvent
import co.com.pagatodo.core.views.base.BaseEvents
import co.com.pagatodo.core.views.base.BaseObservableViewModel
import javax.inject.Inject

class PayBillViewModel: ViewModel() {

    @Inject
    lateinit var interactor: IPayBillInteractor
    lateinit var agreementList: MutableLiveData<List<AgreementModel>>
    lateinit var bill: MutableLiveData<BillModel>

    sealed class ViewEvent {
        class ResponseError(val errorMessage: String?): ViewEvent()
        class ResponseSuccess(val successMessage: String?): ViewEvent()
    }

    var singleLiveEvent: SingleLiveEvent<ViewEvent> = SingleLiveEvent()

    init {
        DaggerPayBillComponent.builder()
            .payBillModule(PayBillModule()).build()
            .inject(this)

        if (!::agreementList.isInitialized){
            agreementList = MutableLiveData()
        }
        if (!::bill.isInitialized){
            bill = MutableLiveData()
        }
    }

    fun dispatchGetAgreements(){
        getAgreements()
    }

    fun dispatchGetBill(serviceId: String, nameService: String, userCode: String){
        getBill(serviceId, nameService, userCode)
    }

    fun dispatchPayBill(companyId:String ,barCodeService: String,serviceName:String){
        val billModel = bill.value ?: BillModel()
        val request = PayBillRequestBillModel().apply{
            serviceId = billModel.serviceId
            barCode = barCodeService
            isEntry = billModel.billService?.isEntry
            loadType = billModel.billService?.loadType
            totalExpiredDays = billModel.billService?.totalExpiredDays
            payReference = barCodeService
            expirationDate = billModel.expirationDate
            billValue = billModel.billValue
            valueToPay = billModel.valueToPay
            billNumber = billModel.codeSeqBill
            userName = billModel.userName
            flatIdBill = billModel.flatIdBill
            isBillExpirated = billModel.isBillExpirated
            billStatus = billModel.billStatus
            accountNumber = billModel.accountNumber
            codeSeqBill = billModel.codeSeqBill
            this.companyId = billModel.companyId
            this.serviceName = serviceName

        }

        payBill(request)
    }

    fun getAgreementDescription(position: Int): DescriptionAgreementModel? {
        return (agreementList.value ?: arrayListOf())[position].description
    }

    fun getBill(): BillModel? {
        return bill.value
    }
    fun deleteBill(){
        bill.value = BillModel().apply { serviceId = "0" }
    }
}

@SuppressLint("CheckResult")
private fun PayBillViewModel.getAgreements(){
    interactor.getAgreements()?.subscribe({
        if (it.isSuccess == true){
            agreementList.value = it.agreements
        }else{
            singleLiveEvent.value = PayBillViewModel.ViewEvent.ResponseError(it.message)
        }
    },{
        singleLiveEvent.value = PayBillViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
    })
}

@SuppressLint("CheckResult")
private fun PayBillViewModel.getBill(serviceId: String, nameService: String, userCode: String){
    interactor.getBill(serviceId,userCode)?.subscribe({
        if (it.isSuccess == true){
            val currentBill = it.bill
            if (currentBill?.nameService?.isEmpty() == true){
                currentBill.nameService = nameService
            }
            if (currentBill?.userCode?.isEmpty() == true){
                currentBill.userCode = userCode
            }
            if (currentBill?.barCode?.isEmpty() == true){
                currentBill.barCode = userCode
            }
            if (currentBill?.companyId?.isEmpty() == true){
                currentBill.companyId = serviceId
            }
            bill.value = currentBill
        }else{
            singleLiveEvent.value = PayBillViewModel.ViewEvent.ResponseError(it.message)
        }
    },{
        singleLiveEvent.value = PayBillViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
    })
}

@SuppressLint("CheckResult")
private fun PayBillViewModel.payBill(request: PayBillRequestBillModel, isRetry: Boolean = true, transactionType: String? = null){
    val billModel = bill.value ?: BillModel()
    interactor.payBill(request)?.subscribe({
        if (it.isSuccess == true){

            val printModel = PayBillPrintModel().apply {
                date = it.payBill?.lstBills?.first()?.collectionDate
                hour = it.payBill?.lstBills?.first()?.collectionHour
                voucherNumber = it.payBill?.lstBills?.first()?.voucherNumber
                billNumber = request.billNumber
                referenceCode = it.payBill?.lstBills?.first()?.codeSeqBill
                code = request.billNumber
                descriptionService = it.payBill?.lstBills?.first()?.description
                valueToPay = it.payBill?.lstBills?.first()?.valueToPay?.toDouble()
                totalValue = it.payBill?.lstBills?.first()?.billValue?.toDouble()
                header = billModel.billService?.printFragment
            }

            interactor.print(printModel, isRetry) { status ->
                if (status == PrinterStatus.OK){
                    BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", it.message ?: R_string(R.string.text_success_transaction), true))
                }else{
                    BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", it.message ?: R_string(R.string.message_error_print_device), false))
                }
            }
        }else{
            singleLiveEvent.value = PayBillViewModel.ViewEvent.ResponseError(it.message ?: R_string(R.string.message_error_transaction))
        }
    },{
        singleLiveEvent.value = PayBillViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
    })
}

@SuppressLint("CheckResult")
private fun PayBillViewModel.rePrintPayBill(billModel: BillModel){
    interactor.rePrintPayBill(billModel.accountNumber ?: "",billModel.collectionDate ?: "",billModel.teamVol ?: "",billModel.macAddress ?: "")?.subscribe({
        if (it.isSuccess == true){
            singleLiveEvent.value = PayBillViewModel.ViewEvent.ResponseSuccess(it.message)
        }else{
            singleLiveEvent.value = PayBillViewModel.ViewEvent.ResponseError( it.message ?: R_string(R.string.message_error_transaction))
        }
    },{
        singleLiveEvent.value = PayBillViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
    })
}