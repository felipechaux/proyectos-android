package co.com.pagatodo.core.views.sporting

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.interactors.SportingInteractor
import co.com.pagatodo.core.data.model.SportingBetModel
import co.com.pagatodo.core.data.model.SportingProductModel
import co.com.pagatodo.core.data.model.print.SportingPrintModel
import co.com.pagatodo.core.data.model.request.RequestSellSportingBetModel
import co.com.pagatodo.core.data.model.request.RequestSportingModel
import co.com.pagatodo.core.data.model.request.SportingEventSellModel
import co.com.pagatodo.core.data.model.request.SportingProductSellModel
import co.com.pagatodo.core.data.print.IPrinterStatusInfo
import co.com.pagatodo.core.data.print.PrintManager
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.DaggerSportingComponent
import co.com.pagatodo.core.di.PrinterStatusModule
import co.com.pagatodo.core.di.SportingModule
import co.com.pagatodo.core.util.CurrencyUtils
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.views.SingleLiveEvent
import co.com.pagatodo.core.views.base.*
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.text.FieldPosition
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SportingViewModel: BaseViewModel() {

    sealed class ViewEvent {
        class ResponseMessage(val message: String?, val backToMenu: Boolean?): ViewEvent()
    }

    @Inject
    lateinit var sportingInteractor: SportingInteractor
    @Inject
    lateinit var printerStatusInfo: IPrinterStatusInfo

    lateinit var sportingProduct: MutableLiveData<SportingProductModel>
    lateinit var sportingEventModel: MutableLiveData<List<SportingBetModel>>
    private var temporalListBets:List<SportingBetModel> = arrayListOf()
    var accumulated = 0
    var closeDate = ""
    var codeGrid = ""
    var ticketHeaderMegagoal = ""
    var ticketHeaderLeague = ""
    var contract = ""
    var productNameMegagoal = ""
    var productNameLeague = ""
    var ticketFooterMegagoal = ""
    var ticketFooterLeague = ""

    var singleLiveEvent: SingleLiveEvent<ViewEvent> =
        SingleLiveEvent()

    init {
        DaggerSportingComponent
            .builder()
            .sportingModule(SportingModule())
            .printerStatusModule(PrinterStatusModule())
            .build().inject(this)

        if (!::sportingProduct.isInitialized){
            sportingProduct = MutableLiveData()
            sportingEventModel = MutableLiveData()
        }

        getProductParameters()
    }

    fun getParameters(productCode: Int){
        getParams(productCode)
    }

    fun sellMegaGoaldBet(request: RequestSportingModel, isRetry: Boolean = true, transactionType: String? = null){
        temporalListBets = sportingEventModel.value?.filter { it.isVisitorResult == true && it.isLocalResult == true } ?: arrayListOf()
        sellSportingBets(request, isRetry, transactionType)
    }

    fun getTeamsForMegaGoalSummary(): List<SportingBetModel> {
        val list = mutableListOf<SportingBetModel>()
        sportingEventModel.value?.forEach {
            if ((it.isVisitorResult ?: false) && (it.isLocalResult ?: false)){
                val model = SportingBetModel().apply {
                    this.isLocalResult = false
                    this.isVisitorResult = false
                    this.betId = it.betId
                    this.code = it.code
                    this.local = it.local
                    this.localName = it.localName
                    this.visitor = it.visitor
                    this.visitorName = it.visitorName
                    this.date = it.date
                    this.time = it.time
                    this.isTieResult = it.isTieResult
                    this.localMarker = it.localMarker
                    this.visitorMarker = it.visitorMarker
                }

                list.add(model)
            }
        }
        return list.toList()
    }

    fun generateRandomGameLeague14(){
        sportingEventModel.value?.forEach {
            it.isLocalResult = false
            it.isTieResult = false
            it.isVisitorResult = false
            val random = Random().nextInt(3)
            when(random){
                0 -> { it.isLocalResult = true }
                1 -> { it.isTieResult = true }
                2 -> { it.isVisitorResult = true }
            }

        }
    }

    fun generateRandomGameMegaGoal(){

        val random = Random()

        val randomTeamOne = random.nextInt(5)
        var randomTeamSecond = random.nextInt(5)

        if (randomTeamOne == randomTeamSecond){
            randomTeamSecond += 1
        }

        sportingEventModel.value?.forEachIndexed { index, item ->
            if (index == randomTeamOne || index == randomTeamSecond){
                item.isLocalResult = true
                item.isVisitorResult = true
                item.visitorMarker = random.nextInt(9)
                item.localMarker = random.nextInt(9)
            }else{
                item.isLocalResult = false
                item.isVisitorResult = false
                item.visitorMarker = 0
                item.localMarker = 0
            }
        }
    }

    fun validateAllGamesIsChecked(): Boolean {
        sportingEventModel.value?.forEach {
            if (!(it.isLocalResult ?: false) && !(it.isTieResult ?: false) && !(it.isVisitorResult ?: false)){
                return false
            }
        }
        return true
    }

    fun validatePositionGamesIsChecked(position: Int): Boolean {
        sportingEventModel.value?.forEachIndexed { index, sportingBetModel ->

            if(index <= position){
                if (!(sportingBetModel.isLocalResult ?: false) && !(sportingBetModel.isTieResult ?: false) && !(sportingBetModel.isVisitorResult ?: false)){
                    return false
                }
            }

        }
        return true
    }

    fun getBetValue(): Int {
       return sportingProduct.value?.events?.first()?.betValue ?: 0
    }

    fun getIvaBet(): Double {
        return (getBetValue().toDouble() / CurrencyUtils.getIvaPercentageSporting())
    }

    fun clearMarkers(){
        sportingEventModel.value?.forEach {
            it.isVisitorResult = false
            it.isLocalResult = false
            it.localMarker = 0
            it.visitorMarker = 0
        }
    }

    fun clearResults(){
        sportingEventModel.value?.forEach{
            it.isLocalResult = false
            it.isTieResult = false
            it.isVisitorResult = false
        }
    }
}

@SuppressLint("CheckResult")
private fun SportingViewModel.getProductParameters(){
    sportingInteractor.getProductParameters()?.subscribe ({
        it.forEach{
            if(it.key.equals(R_string(R.string.name_column_header_megagoal))) {
                ticketHeaderMegagoal = it.value ?: ""
            }
            if(it.key.equals(R_string(R.string.name_column_header_league))) {
                ticketHeaderLeague = it.value ?: ""
            }
            if(it.key.equals(R_string(R.string.name_column_ticket_product_mega))) {
                productNameMegagoal = it.value ?: ""
            }
            if(it.key.equals(R_string(R.string.name_column_product))) {
                productNameLeague = it.value ?: ""
            }
            if(it.key.equals(R_string(R.string.name_column_footer_megagoal))) {
                ticketFooterMegagoal = it.value ?: ""
            }
            if(it.key.equals(R_string(R.string.name_column_footer_league))) {
                ticketFooterLeague = it.value ?: ""
            }
            if (it.key.equals(R_string(R.string.name_column_product_contract))){
                contract = it.value ?: ""
            }
        }
    },{
        if (it is ConnectException){
            singleLiveEvent.value = SportingViewModel.ViewEvent.ResponseMessage(R_string(R.string.message_no_network), true)
        }
        else if (it !is SocketTimeoutException) {
            singleLiveEvent.value =
                    SportingViewModel.ViewEvent.ResponseMessage(R_string(R.string.message_error_bet), true)
        }
    })
}

@SuppressLint("CheckResult", "DefaultLocale")
private fun SportingViewModel.getParams(productCode: Int){
    sportingInteractor.getParameters()?.subscribe({

        val products = (it.products?.filter { it.productId == productCode } ?: arrayListOf()).first()
        val events = products?.events?.first()
        val bets = events?.bets
        val dateClose = Date((events?.closeDate ?: "").toLong())

        val arrayTime = events?.closeTime?.split(":") ?: arrayListOf()
        val time = "${arrayTime[0]}:${arrayTime[1]}"

        accumulated = events?.accumulated ?: 0
        closeDate = DateUtil.convertDateToStringFormat(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH.rawValue,dateClose).toUpperCase() + " " + time
        codeGrid = events?.code ?: ""

        sportingProduct.value = products
        sportingEventModel.value = bets
    },{
        if (it is ConnectException){
            singleLiveEvent.value = SportingViewModel.ViewEvent.ResponseMessage(R_string(R.string.message_no_network), true)
        }
        else if (it !is SocketTimeoutException) {
            singleLiveEvent.value = SportingViewModel.ViewEvent.ResponseMessage(R_string(R.string.message_error_bet), true)
        }
    })
}

fun SportingViewModel.sellSportingBets(request: RequestSportingModel, isRetry: Boolean = true, transactionType: String? = null){
    validatePrinterStatus(printerStatusInfo){
        executePaySporting(request, isRetry, transactionType)
    }
}

@SuppressLint("CheckResult")
private fun SportingViewModel.executePaySporting(request: RequestSportingModel, isRetry: Boolean = true, transactionType: String? = null){

    if (transactionType == null)
        saveLastTransaction(request, request.productStatusCode)

    val listBets: List<SportingBetModel>
    if (request.productCode == R_string(R.string.code_sporting_mega_goals_product).toInt()){
        listBets = getTeamsForMegaGoalSummary()
    }else{
        listBets = sportingEventModel.value ?: arrayListOf()
    }

    sportingInteractor.sellSportingBet(RequestSellSportingBetModel().apply {
        value = getBetValue()
        event = SportingEventSellModel().apply {
            id = sportingProduct.value?.events?.first()?.eventId
            bets = listBets
        }
        product = SportingProductSellModel().apply{
            id = sportingProduct.value?.productId
        }
        transactionTime = request.transactionTime
        sequenceTransaction = request.sequenceTransaction
    }, isRetry, transactionType)?.subscribe({

        if (it.isSuccess ?: false){
            val header = if(request.productCode == R_string(R.string.code_sporting_league_product).toInt()) ticketHeaderLeague else ticketHeaderMegagoal
            val footer = if(request.productCode == R_string(R.string.code_sporting_league_product).toInt()) ticketFooterLeague else ticketFooterMegagoal
            val productName = if(request.productCode == R_string(R.string.code_sporting_league_product).toInt()) productNameLeague else productNameMegagoal
            val transactionDate = it.date+"-"+it.hour
            val grid = closeDate.split(" ")[0]
            val stub = it.serie1 + "  " + it.consecutive
            val checkedCode = CurrencyUtils.generateRandomAlphanumericString(14)
            var teams = sportingEventModel.value
            if(request.productCode ==  R_string(R.string.code_sporting_mega_goals_product).toInt()){
                teams = teams?.filter { it.isLocalResult ?: false && it.isVisitorResult ?: false }
            }
            val totalValue = it.totalValue
            val iva = it.valueIva
            val value = it.valueNeto
            val digitCheck = it.verificationCode

            val sportinModel = SportingPrintModel().apply {
                this.header = header
                this.footer = footer
                this.productName = productName
                this.productCode = request.productCode
                this.contractDate = contract
                this.dateOfSale = transactionDate
                this.grid = grid
                this.stub = stub
                this.digitChecked = checkedCode
                this.teams = teams
                this.totalValue = totalValue.toString()
                this.iva = iva.toString()
                this.digitCheck = digitCheck
                this.value = value.toString()
                this.imageFooter = request.image
            }

            StorageUtil.updateStub(it.serie1 ?: "", it.currentSerie2 ?: "")
            sportingInteractor.print(sportinModel, transactionType != null){
                if (it == PrinterStatus.OK){
                    
                    BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", R_string(R.string.message_success_bet), true))
                }else{
                    BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", R_string(R.string.message_error_print_device)))
                }
            }
        }else{
            removeLastTransaction()
            BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", (it.messages?.first()?.message) ?: R_string(R.string.message_error_transaction)))
        }
    },{
        removeLastTransaction(it)
        if (it is ConnectException){
            singleLiveEvent.value = SportingViewModel.ViewEvent.ResponseMessage(R_string(R.string.message_no_network), true)
        }
        else if (it !is SocketTimeoutException) {
            singleLiveEvent.value =
                    SportingViewModel.ViewEvent.ResponseMessage(R_string(R.string.message_error_transaction), true)
        }
        BaseObservableViewModel.baseSubject.onNext(BaseEvents.ShowAlertDialogInMenu("", R_string(R.string.message_error_transaction)))
    })
}
