package co.com.pagatodo.core.views.raffle

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.response.ResponseRaffleAvailableRangeDTO
import co.com.pagatodo.core.data.interactors.RaffleInteractor
import co.com.pagatodo.core.data.model.ProductName
import co.com.pagatodo.core.data.model.RaffleModel
import co.com.pagatodo.core.data.model.request.RequestRaffleModel
import co.com.pagatodo.core.data.print.IPrinterStatusInfo
import co.com.pagatodo.core.data.print.PrintManager
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.DaggerRaffleComponent
import co.com.pagatodo.core.di.PrinterStatusModule
import co.com.pagatodo.core.di.RaffleModule
import co.com.pagatodo.core.util.CurrencyUtils
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.SingleLiveEvent
import co.com.pagatodo.core.views.base.*
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RaffleViewModel: BaseViewModel() {

    sealed class ViewEvent {
        class ResponseMessage(val message: String, val isSuccess: Boolean = false): ViewEvent()
        class ResponseRandomNumber(val number: String): ViewEvent()
        class ResponseNumberSale(val message: String, val isSuccess: Boolean): ViewEvent()
        class AvailableRangeSuccess(val response: ResponseRaffleAvailableRangeDTO, val startNum: String, val endNum: String): ViewEvent()
    }

    @Inject
    lateinit var raffleInteractor: RaffleInteractor
    @Inject
    lateinit var printerStatusInfo: IPrinterStatusInfo
    lateinit var rafflesListLiveData: MutableLiveData<List<RaffleModel>>

    internal var currentRaffle: RaffleModel? = null
    var singleLiveEvent: SingleLiveEvent<ViewEvent> = SingleLiveEvent()

    init {
        DaggerRaffleComponent
            .builder()
            .raffleModule(RaffleModule())
            .printerStatusModule(PrinterStatusModule())
            .build().inject(this)

        if (!::rafflesListLiveData.isInitialized){
            rafflesListLiveData = MutableLiveData()
        }
    }

    fun setCurrentRaffle(raffle: RaffleModel?) {
        currentRaffle = raffle
    }

    fun getCurrentRaffle() = currentRaffle

    fun isSelectedRaffle(): Boolean {
        return currentRaffle != null
    }

    fun fetchRaffles(){
        getRafflesFromLocal()
    }

    fun fetchRandomNumber(){
        generateRandomNumber()
    }

    fun fetchAvailableRaffles(number:String){
        getAvailableRaffle(number)
    }

    fun availableRange(codRaffle:String, startNum:String, endNum :String){
        raffleAvailableRange(codRaffle,startNum,endNum)
    }

    fun getRequestRaffleModel(): RequestRaffleModel {
        return RequestRaffleModel().apply {
            raffleId = currentRaffle?.raffleId
            numberSale = currentRaffle?.raffleNumber
            transactionTime = DateUtil.getCurrentDateInUnix()
            sequenceTransaction = StorageUtil.getSequenceTransaction()
        }
    }

    fun sellRaffleNumber(request: RequestRaffleModel, isRetry: Boolean = true, transactionType: String? = null) {
        validatePrinterStatus(printerStatusInfo){
            executePayRaffle(request, isRetry, transactionType)
        }
    }

    @SuppressLint("CheckResult")
    fun executePayRaffle(request: RequestRaffleModel, isRetry: Boolean = true, transactionType: String? = null){

        if (transactionType == null)
            saveLastTransaction(request, ProductName.RAFFLE.rawValue)

        val raffleId = request.raffleId ?: 0
        val number = request.numberSale ?: ""

        raffleInteractor.payRaffle(raffleId, number, isRetry, transactionType, request.transactionTime, request.sequenceTransaction)?.subscribe({


            val serie1 = it.serie1
            val currentSerie2 = it.currentSerie2

            if (it.isSuccess == true){
                val message = it.message ?: R_string(R.string.text_raffles_number_register_error)
                raffleInteractor.print(it.uniqueSerial ?: "", CurrencyUtils.generateRandomAlphanumericString(12),
                    currentRaffle?.raffleName ?: "", number,
                    currentRaffle?.lotteryName ?: "",
                    formatValue(currentRaffle?.price?.toDouble() ?: 0.0),
                    formatValue(currentRaffle?.pricePrize?.toDouble() ?: 0.0),
                    currentRaffle?.dateDraw ?: "",
                    currentRaffle?.drawTime?: "",
                    currentRaffle?.raffleDescription ?: "", isRetry){
                    StorageUtil.updateStub(serie1 ?: "", currentSerie2 ?: "")
                    if (it == PrinterStatus.OK){

                        BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", R_string(R.string.text_raffles_number_register_success), true))
                    }else{
                        BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", message))
                    }
                }
            }else{
                removeLastTransaction()
                BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", it.message ?: R_string(R.string.message_error_transaction)))
            }
        },{
            removeLastTransaction(it)
            if (it is ConnectException){
                singleLiveEvent.value = RaffleViewModel.ViewEvent.ResponseMessage(R_string(R.string.message_no_network))
            }
            else if (it !is SocketTimeoutException) {
                singleLiveEvent.value = RaffleViewModel.ViewEvent.ResponseNumberSale(
                    R_string(R.string.text_raffles_number_register_error),
                    false
                )
            }
        })
    }
}

@SuppressLint("CheckResult")
private fun RaffleViewModel.getRafflesFromLocal(){
    raffleInteractor.getAllRafflesFromLocal()?.subscribe ({
        if (!it.isEmpty()){
            rafflesListLiveData.postValue(it)
        }else{
            singleLiveEvent.postValue(
                RaffleViewModel.ViewEvent.ResponseMessage(R_string(R.string.text_raffles_list_error))
            )
        }
    },{
        if (it is ConnectException){
            singleLiveEvent.postValue(RaffleViewModel.ViewEvent.ResponseMessage(R_string(R.string.message_no_network)))
        }
        else if (it !is SocketTimeoutException) {
            singleLiveEvent.postValue(
                RaffleViewModel.ViewEvent.ResponseMessage(R_string(R.string.text_raffles_number_register_error))
            )

        }
    })
}

@SuppressLint("CheckResult")
private fun RaffleViewModel.generateRandomNumber(){
    if (currentRaffle != null){
        raffleInteractor.getRandomRaffle(currentRaffle?.raffleId.toString())?.subscribe({
            val isSuccess = it.isSuccess ?: false
            if (isSuccess){
                singleLiveEvent.value = RaffleViewModel.ViewEvent.ResponseRandomNumber(it.number ?: "")
            }else{
                singleLiveEvent.value = RaffleViewModel.ViewEvent.ResponseMessage(it.message?:R_string(R.string.text_raffles_error_random_number))
            }
        },{
            if (it is ConnectException){
                singleLiveEvent.value = RaffleViewModel.ViewEvent.ResponseMessage(R_string(R.string.message_no_network))
            }
             else if (it !is SocketTimeoutException) {
                singleLiveEvent.value = RaffleViewModel.ViewEvent.ResponseMessage(R_string(R.string.text_raffles_error_random_number))
            }
        })
    }else{
        singleLiveEvent.value = RaffleViewModel.ViewEvent.ResponseMessage(R_string(R.string.text_raffles_validation_id))
    }
}

@SuppressLint("CheckResult")
private fun RaffleViewModel.getAvailableRaffle(numberRaffle:String){
    if (validateNumber(numberRaffle)){
        raffleInteractor.getRaffleAvailable(currentRaffle?.raffleId.toString(), numberRaffle)?.subscribe({
            val isSuccess = it.responseCode.equals("0")
            val message: String
            if (isSuccess){
                currentRaffle?.raffleNumber = numberRaffle
                message = R_string(R.string.text_raffles_available_number)
            }else{
                message = it.message ?: R_string(R.string.text_raffles_not_available_number)
            }
            singleLiveEvent.value = RaffleViewModel.ViewEvent.ResponseMessage(message, isSuccess)
        },{
            if (it is ConnectException){
                singleLiveEvent.value = RaffleViewModel.ViewEvent.ResponseMessage(R_string(R.string.message_no_network))
            }
            else if (it !is SocketTimeoutException) {
                singleLiveEvent.value = RaffleViewModel.ViewEvent.ResponseMessage(it.localizedMessage, false)
            }
        })
    }
}

fun RaffleViewModel.validateNumber(raffleNumber: String): Boolean{
    if (raffleNumber.isEmpty()){
        singleLiveEvent.value = RaffleViewModel.ViewEvent.ResponseMessage(R_string(R.string.text_raffles_validation_empty_number))
        return false
    }else if (raffleNumber.length != currentRaffle?.raffleNumber?.length){
        singleLiveEvent.value = RaffleViewModel.ViewEvent.ResponseMessage(R_string(R.string.text_raffles_validation_count))
        return false
    }
    return true
}

@SuppressLint("CheckResult")
private fun RaffleViewModel.raffleAvailableRange(codRaffle:String, startNum:String, endNum :String){

    raffleInteractor.raffleAvailableRange(codRaffle,startNum,endNum)?.subscribe({
        singleLiveEvent.value = RaffleViewModel.ViewEvent.AvailableRangeSuccess(it, startNum, endNum)

    },{
        singleLiveEvent.value = RaffleViewModel.ViewEvent.ResponseMessage(R_string(R.string.message_error_transaction),false)

    })


}



