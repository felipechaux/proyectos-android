package co.com.pagatodo.core.views.colpensionesbeps

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.interactors.IColpensionesBepsInteractor
import co.com.pagatodo.core.data.interactors.ILocalInteractor
import co.com.pagatodo.core.data.model.print.ColpensionesBepsPrintModel
import co.com.pagatodo.core.data.model.request.RequestColpensionesBepsMakeCollectionModel
import co.com.pagatodo.core.data.model.request.RequestColpensionesBepsQueryCollectionModel
import co.com.pagatodo.core.data.model.response.ResponseColpensionesBepsCollectionModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.DaggerColpensionesBepsComponent
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.SingleLiveEvent
import co.com.pagatodo.core.views.base.BaseEvents
import co.com.pagatodo.core.views.base.BaseObservableViewModel
import javax.inject.Inject

class ColpensionesBepsViewModel: ViewModel() {

    sealed class ViewEvent {
        class ResponseError(var message: String?): ViewEvent()
        class ResponseSuccess(var message: String?): ViewEvent()
        class LoadParameters(val documents: List<String>) : ViewEvent()
    }

    @Inject
    lateinit var interactor: IColpensionesBepsInteractor

    @Inject
    lateinit var localInteractor: ILocalInteractor

    var singleLiveEvent: SingleLiveEvent<ViewEvent> = SingleLiveEvent()

    var collectionModel: ResponseColpensionesBepsCollectionModel? = null

    var textHeaderProduct = ""
    var textFooterProduct = ""
    var minValue = 5000
    var arrayTypeTransaction = mutableListOf<String>()

    init {
        DaggerColpensionesBepsComponent
            .builder()
            .build()
            .inject(this)
    }

    fun dispatchGetProductParameters(){
        getProductParameters()
    }

    fun dispatchMakeCollection(request: RequestColpensionesBepsMakeCollectionModel){
        makeCollection(request)
    }

    fun dispatchQueryCollection(request: RequestColpensionesBepsQueryCollectionModel){
        queryCollection(request)
    }

    fun getDocumentsType(): List<String> {
        val listToReturn = mutableListOf<String>()
        listToReturn.add(R_string(R.string.placeholder_select_document_type))
        val documents = getPreference<String>(R_string(R.string.documents_param_service)).split(",")
        documents.forEach {
            val item = it.split("-")[0]
            listToReturn.add(item)
        }
        return listToReturn
    }

    @SuppressLint("CheckResult")
    fun getParameter() {

        localInteractor.getProductInfo(RUtil.R_string(R.string.code_product_beps)).subscribe ({
            var returnDocument = it.parameters?.filter { it.key == RUtil.R_string(R.string.beps_parametrer_id_type) }?.map { it.value }?.last()?.split(
                ","
            )!!
            singleLiveEvent.value = ViewEvent.LoadParameters(
                returnDocument
            )

        }, {
            singleLiveEvent.value = ColpensionesBepsViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
        })

    }
}

@SuppressLint("CheckResult")
private fun ColpensionesBepsViewModel.getProductParameters(){
    interactor.getProductParameters()?.subscribe({
        it.forEach{
            if(it.key.equals(R_string(R.string.colpensiones_beps_min_value))) {
                minValue = it.value?.toInt() ?: 5000
            }
            if(it.key.equals(R_string(R.string.name_column_header_colpensiones_beps))) {
                textHeaderProduct = it.value ?: ""
            }
            if(it.key.equals(R_string(R.string.name_column_footer_colpensiones_beps))) {
                textFooterProduct = it.value ?: ""
            }
            if(it.key.equals(R_string(R.string.name_column_transaction_types_colpensiones_beps))) {
                arrayTypeTransaction.clear()
                arrayTypeTransaction.add(R_string(R.string.placeholder_transaction_type))
                it.value?.split(";")?.forEach { item ->
                    arrayTypeTransaction.add(item)
                }
            }
            arrayTypeTransaction
        }
    },{})
}

@SuppressLint("CheckResult")
private fun ColpensionesBepsViewModel.makeCollection(request: RequestColpensionesBepsMakeCollectionModel){
    interactor.makeCollection(request)?.subscribe({
        if (it.isSuccess == true){
            StorageUtil.updateStub(it.serie1 ?: "", it.currentSerie2 ?: "")
            collectionModel = it
            printTicket(it, false, false)
        }else{
            singleLiveEvent.value = ColpensionesBepsViewModel.ViewEvent.ResponseError(it.message)
        }
    },{
        singleLiveEvent.value = ColpensionesBepsViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
    })
}

@SuppressLint("CheckResult")
private fun ColpensionesBepsViewModel.queryCollection(request: RequestColpensionesBepsQueryCollectionModel){
    interactor.queryCollection(request)?.subscribe({
        if (it.isSuccess == true){
            collectionModel = it
            printTicket(it, true, false)
        }else{
            singleLiveEvent.value = ColpensionesBepsViewModel.ViewEvent.ResponseError(it.message)
        }
    },{
        singleLiveEvent.value = ColpensionesBepsViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
    })
}

private fun ColpensionesBepsViewModel.printTicket(response: ResponseColpensionesBepsCollectionModel, isReprint: Boolean, isRejected: Boolean){
    val printModel = ColpensionesBepsPrintModel().apply {
        header = textHeaderProduct
        footer = textFooterProduct
        transactionNumber = response.transactionIdResponse
        authNumber = response.authNumber
        voucherNumber = response.voucherNumber
        userId = response.documentNumber
        userName = response.beneficiaryName
        value = response.value
        transactionDate = response.transactionDate
        transactionHour = response.transactionHour
        printText = response.printTransactionMessage
        rentCode = response.rentCode
        rejectionReason = response.rejectionReason
        this.isReprint = isReprint
        this.isRejected = isRejected
    }

    interactor.print(printModel){ status ->
        if (status == PrinterStatus.OK){
            if(isReprint)
                BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", R_string(R.string.text_success_transaction), true))
            else {
                var successText = ""
                if(isRejected){
                    successText = R_string(R.string.message_error_transaction)
                }else{
                    successText = R_string(R.string.title_success_collection_value_1).plus(" ")
                        .plus(collectionModel?.beneficiaryName).plus(" ")
                        .plus(R_string(R.string.title_succes_collections_value_2)).plus(" $")
                        .plus(formatValue(collectionModel?.value?.toDouble() ?: 0.0))
                }


                BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu(R_string(R.string.title_success_collection), successText, true))

            }
        }else{
            BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", R_string(R.string.message_error_transaction), false))
        }
    }
}