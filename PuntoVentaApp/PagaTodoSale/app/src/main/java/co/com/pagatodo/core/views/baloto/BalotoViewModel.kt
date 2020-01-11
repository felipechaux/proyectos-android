package co.com.pagatodo.core.views.baloto

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.BalotoBoardDTO
import co.com.pagatodo.core.data.dto.BalotoDrawDTO
import co.com.pagatodo.core.data.interactors.BalotoInteractor
import co.com.pagatodo.core.data.model.*
import co.com.pagatodo.core.data.model.request.RequestBalotoModel
import co.com.pagatodo.core.data.model.response.ResponseBalotoCheckTicketModel
import co.com.pagatodo.core.data.model.response.ResponseChargeTicketModel
import co.com.pagatodo.core.data.print.IPrinterStatusInfo
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.BalotoModule
import co.com.pagatodo.core.di.DaggerBalotoComponent
import co.com.pagatodo.core.di.PrinterStatusModule
import co.com.pagatodo.core.util.CurrencyUtils.Companion.isNumeric
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.StorageUtil
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.views.SingleLiveEvent
import co.com.pagatodo.core.views.base.*
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BalotoViewModel : BaseViewModel() {

    sealed class ViewEvent {
        class ShowMessage(val message: String, val isSuccess: Boolean) : ViewEvent()
        class GetDraws(val draws: List<BalotoDrawDTO>) : ViewEvent()
        class ResponsTicketSerie(val checkTicket: ResponseBalotoCheckTicketModel) : ViewEvent()
        class ValidationReturn(val response: Pair<Boolean, String>) : ViewEvent()
        class GetBalotoResults(val balotoResults: List<BalotoResultsModel>) : ViewEvent()
        class ResponseError(val errorMessage: String?) : ViewEvent()
    }

    @Inject
    lateinit var printerStatusInfo: IPrinterStatusInfo
    @Inject
    lateinit var interactor: BalotoInteractor

    lateinit var parametersModel: BalotoParametersModel
    var betDuration = 1
    var maxDuration = 9
    var maxBoards = 5
    var primarySelectionsLowNumber = 1
    var primarySelectionsHighNumber = 43
    var secondarySelectionsLowNumber = 1
    var secondarySelectionsHighNumber = 16
    var basePrice = 5700

    lateinit var boardListLiveData: MutableLiveData<MutableList<BalotoBoardModel>>

    var singleLiveEvent: SingleLiveEvent<ViewEvent> = SingleLiveEvent()

    init {
        DaggerBalotoComponent
            .builder()
            .balotoModule(BalotoModule())
            .printerStatusModule(PrinterStatusModule())
            .build()
            .inject(this)

        if (!::boardListLiveData.isInitialized) {
            boardListLiveData = MutableLiveData()
        }
    }

    fun getParameters() {
        getBalotoParameters()
    }

    fun getResults(reportString: String) {
        getBalotoResults(reportString)
    }

    fun getAccumulated() {
        getBalotoAccumulated()
    }

    fun checkNumberTicket(ticketSerialNumber: String) {
        checkeTicketState(ticketSerialNumber)
    }

    fun setAutomaticBoard(index: Int, isAutomatic: Boolean) {
        boardListLiveData.value?.get(index)?.quickpick = isAutomatic
    }

    fun isInitializedParameters(): Boolean {
        return ::parametersModel.isInitialized
    }

    fun saveLocalPameter() {
        interactor.saveLocalParameterBaloto(parametersModel)
    }

    fun getTotalBetWithOutRematch(): Int {
        var value = 0
        if (isInitializedParameters() && parametersModel.addonGames != null) {
            boardListLiveData.value?.forEach {
                value = value + (parametersModel.basePrice ?: 0)
            }
        }

        return value * betDuration
    }

    fun getTotalBetRematch(): Int {
        var value = 0
        if (isInitializedParameters() && parametersModel.addonGames != null) {
            boardListLiveData.value?.forEach {
                if (it.addonPlayed ?: false) {
                    value = value + getRematchBasePrice()
                }
            }
        }

        return value * betDuration
    }

    fun getTotalBet(): Int {
        var value = 0
        if (isInitializedParameters() && parametersModel.addonGames != null) {
            boardListLiveData.value?.forEach {
                value = value + (parametersModel.basePrice ?: 0)
                if (it.addonPlayed ?: false) {
                    value = value + getRematchBasePrice()
                }
            }
        }

        return value * betDuration
    }

    fun getRematchBasePrice(): Int {
        val rematch =
            parametersModel.addonGames?.filter { it.gameId.equals(R_string(R.string.baloto_game_id_addonplayed)) }
                ?.first()
        return rematch?.basePrice ?: 0
    }

    fun getBalotoRematchSum(): Int {
        val baloto = parametersModel.basePrice
        val rematch =
            parametersModel.addonGames?.filter { it.gameId.equals(R_string(R.string.baloto_game_id_addonplayed)) }
                ?.first()

        return (baloto ?: 0) + (rematch?.basePrice ?: 0)
    }

    fun getNumbersFromList(): Pair<String, String> {
        var titleBet = ""
        var bets = ""
        boardListLiveData.value?.forEachIndexed { index, board ->

            titleBet = titleBet + "${R_string(R.string.baloto_text_panel)} ${index + 1}: "

            if (board.addonPlayed ?: false) {
                titleBet = titleBet + R_string(R.string.dialog_baloto_br)
            } else {
                titleBet = titleBet + R_string(R.string.dialog_baloto_b)
            }

            if (index < (boardListLiveData.value?.count() ?: 1) - 1) {
                titleBet = titleBet + "\n"
            }

            var numberBet = ""

            board.selections?.forEachIndexed { index, item ->

                var temNum = item

                if (temNum.length == 1) {
                    temNum = if (temNum == "*") temNum else "0$temNum"
                }

                numberBet = if (index == 5) {
                    "${numberBet} - ${temNum}"
                } else {
                    "${numberBet} ${temNum}"
                }
            }

            bets = bets + numberBet

            if (board.quickpick ?: false) {
                bets = bets + " ${R_string(R.string.dialog_baloto_au)}"
            }

            if (index < (boardListLiveData.value?.count() ?: 1) - 1) {
                bets = bets + "\n"
            }
        }

        return Pair(titleBet, bets)
    }

    fun validateNumbersFromBoards() {
        var isValid = true
        var validResponse = Pair(true, "")
        boardListLiveData.value
            ?.filter { it.quickpick == false }
            ?.forEach {
                validResponse = isValidBoard(it)
                if (validResponse.first == true) {
                    if (!isNotRepeatedNumber(it.selections ?: arrayOf())) {
                        isValid = false
                        singleLiveEvent.value = BalotoViewModel.ViewEvent.ValidationReturn(
                            Pair(
                                false,
                                R_string(R.string.message_error_numbers_repeat)
                            )
                        )
                        return
                    }
                } else {
                    isValid = false
                    singleLiveEvent.value =
                        BalotoViewModel.ViewEvent.ValidationReturn(validResponse)
                    return
                }
            }
        if (isValid) {
            singleLiveEvent.value = BalotoViewModel.ViewEvent.ValidationReturn(Pair(true, ""))
        } else {
            singleLiveEvent.value = BalotoViewModel.ViewEvent.ValidationReturn(validResponse)
        }
    }

    private fun isValidBoard(board: BalotoBoardModel): Pair<Boolean, String> {
        board.selections?.forEachIndexed { index, number ->

            var numTem = number
            if (numTem.length == 1) {
                numTem = "0" + numTem
            }

            if (index == board.selections?.lastIndex && numTem.isEmpty()) {
                return Pair(false, R_string(R.string.message_error_complete_s_balota))
            } else if (numTem.isEmpty()) {
                return Pair(false, R_string(R.string.message_error_complete_all_fields))
            } else if (isNumeric(numTem)) {
                if (index == board.selections?.lastIndex) {
                    val response = validateNumberBoardS(numTem)
                    if (!response.first) {
                        return response
                    }
                } else {
                    if (numTem.length < 2) {
                        return Pair(false, R_string(R.string.message_error_number_two_digits))
                    } else if (numTem.toInt() > (primarySelectionsHighNumber)) {
                        return Pair(false, R_string(R.string.message_error_out_of_range_high))
                    } else if (numTem.toInt() < (primarySelectionsLowNumber)) {
                        return Pair(false, R_string(R.string.message_error_out_of_range_low))
                    }
                }
            }
        }
        return Pair(true, "")
    }

    private fun validateNumberBoardS(number: String): Pair<Boolean, String> {

        var numTem = number
        if (numTem.length == 1) {
            numTem = "0" + numTem
        }

        if (numTem.isEmpty()) {
            return Pair(false, R_string(R.string.message_error_empty_s_balota))
        } else if (isNumeric(numTem)) {
            if (numTem.length < 2) {
                return Pair(false, R_string(R.string.message_error_number_two_digits))
            } else if (numTem.toInt() > (secondarySelectionsHighNumber)) {
                return Pair(false, R_string(R.string.message_error_out_of_range_high))
            } else if (numTem.toInt() < (secondarySelectionsLowNumber)) {
                return Pair(false, R_string(R.string.message_error_out_of_range_low))
            } else {
                return Pair(true, "")
            }
        } else {
            return Pair(false, R_string(R.string.message_error_empty_s_balota))
        }
    }

    private fun isNotRepeatedNumber(numbers: Array<String>): Boolean {
        val arrayWithOutSuperBalota = arrayListOf<String>()
        for (i in 0..numbers.count() - 2) {
            if (numbers[i].isNotEmpty()) {
                arrayWithOutSuperBalota.add(numbers[i])
            }
        }
        return (arrayWithOutSuperBalota.distinct().count() == arrayWithOutSuperBalota.count())
    }

    fun getRequestBalotoModel(): RequestBalotoModel {
        return RequestBalotoModel().apply {
            this.headerBaloto = parametersModel.productParameters?.headerTicketPv
            this.totalValueWithoutRematch = formatValue(getTotalBetWithOutRematch().toDouble())
            this.totalValueRematch = getTotalBetRematch().toString()
            this.totalWithoutIva = getTotalBet()
            this.total = formatValue(totalWithoutIva + (iva).toDouble())
            this.footerTicket = parametersModel.productParameters?.footerTicketPv
            this.boardList = boardListLiveData.value?.toList()
            this.footerTicket = parametersModel.productParameters?.footerTicketSp
            this.duration = betDuration
            this.transactionTime = DateUtil.getCurrentDateInUnix()
            this.sequenceTransaction = StorageUtil.getSequenceTransaction()
        }
    }

    fun dispatchPayBalotoBet(
        request: RequestBalotoModel,
        isRetry: Boolean = true,
        transactionType: String? = null
    ) {
        validatePrinterStatus(printerStatusInfo) {
            executeBalotoPay(request, isRetry, transactionType)
        }
    }

    @SuppressLint("CheckResult")
    fun executeBalotoPay(
        request: RequestBalotoModel,
        isRetry: Boolean = true,
        transactionType: String? = null
    ) {
        if (transactionType == null)
            saveLastTransaction(request, ProductName.BALOTO.rawValue)

        interactor.sendBalotoBet(
            request.totalWithoutIva, request.duration, request.boardList ?: arrayListOf(),
            isRetry, transactionType, request.transactionTime, request.sequenceTransaction
        )?.subscribe({

            if (it?.isSuccess ?: false) {
                val taxes = it.tax?.first()

                val headerBaloto = request.headerBaloto
                val transactionDate = DateUtil.convertStringToDate(
                    DateUtil.StringFormat.EEE_DD_MM_YY,
                    DateUtil.addBackslashToStringDate(it.transactionDate ?: "")
                ).toUpperCase() + " " + it.transactionHour
                val boards = compareBoardList(it.board, request.boardList ?: arrayListOf())
                val totalValueWithOutRematch = (taxes?.grossAmount)
                val totalValueRematch = (taxes?.taxRate)
                val iva = (taxes?.taxAmount)
                val totalWithOutIva = taxes?.netAmount
                val total = ((taxes?.netAmount ?: 0) + (taxes?.taxAmount ?: 0))
                val drawInfo = getDrawsDate(it, betDuration)
                val serialNumber = it.serialNumber
                val checkDigit = it.checkDigit
                val wagerGuardNumber = it.wagerGuardNumber
                val footerTicket = request.footerTicket

                printBalotoTicketSale(
                    headerBaloto,
                    transactionDate,
                    boards,
                    "$${formatValue(totalValueWithOutRematch?.toDouble() ?: 0.0)}",
                    "$${formatValue(totalValueRematch?.toDouble() ?: 0.0)}",
                    "$${iva?.toDouble()?.let { it1 -> formatValue(it1) }}",
                    "$${formatValue((totalWithOutIva ?: 0).toDouble())}",
                    "$${formatValue(total.toDouble())}",
                    drawInfo,
                    serialNumber,
                    checkDigit,
                    wagerGuardNumber,
                    footerTicket, false
                ) {
                    if (it == PrinterStatus.OK) {

                        BaseObservableViewModel.baseSubject.onNext(
                            BaseEvents.ShowAlertDialogInMenu(
                                "",
                                R_string(R.string.message_success_bet),
                                true
                            )
                        )
                    } else {

                        BaseObservableViewModel.baseSubject.onNext(
                            BaseEvents.ShowAlertDialogInMenu(
                                "",
                                R_string(R.string.message_error_print_device)
                            )
                        )
                    }
                }

                StorageUtil.updateStub(it?.serie1 ?: "", it?.currentSerie2 ?: "")
                StorageUtil.updateSequenceTransaction()

            } else {
                removeLastTransaction()
                BaseObservableViewModel.baseSubject.onNext(
                    BaseEvents.ShowAlertDialogInMenu(
                        "",
                        it.message ?: R_string(R.string.message_error_transaction)
                    )
                )
            }
        }, {
            removeLastTransaction(it)
            if (it is ConnectException) {
                singleLiveEvent.value = BalotoViewModel.ViewEvent.ShowMessage(
                    R_string(R.string.message_no_network),
                    false
                )
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.value = BalotoViewModel.ViewEvent.ShowMessage(
                    R_string(R.string.message_error_bet),
                    false
                )
            }
        })
    }

    @SuppressLint("CheckResult")
    fun checkTiket(numberTicket: String, canPay: Boolean) {

        interactor.chargeTicket(numberTicket, canPay)?.subscribe({

            if (it.isSuccess == true) {

                StorageUtil.updateStub(it?.serie1 ?: "", it?.currentSerie2 ?: "")
                StorageUtil.updateSequenceTransaction()

                val transactionDate = DateUtil.convertStringToDate(
                    DateUtil.StringFormat.EEE_DD_MM_YY,
                    DateUtil.addBackslashToStringDate(it.transactionDate ?: "")
                ).toUpperCase() + " " + it.transactionHour

                interactor.printBalotoQueryTicket(
                    transactionDate,
                    numberTicket,
                    it?.cashAmount ?: "",
                    it.taxes?.first()?.taxAmount.toString(),
                    it?.netAmount ?: "",
                    it?.serialNumber ?: ""
                ) { itl -> chargeTicket(itl, it) }
            } else {
                singleLiveEvent.value = BalotoViewModel.ViewEvent.ShowMessage(
                    it.message ?: R_string(R.string.message_error_transaction), false
                )

            }
        }, {
            if (it is ConnectException) {
                singleLiveEvent.value =
                    BalotoViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
            } else {
                singleLiveEvent.value =
                    BalotoViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
            }
        })
    }

    private fun chargeTicket(itl: PrinterStatus, it: ResponseChargeTicketModel) {

        if (itl == PrinterStatus.OK) {

            if (it.exchangeTicketDTO != null) {

                chargeTicketReprint(it)
                
            } else {

                BaseObservableViewModel.baseSubject.onNext(
                    BaseEvents.ShowAlertDialogInMenu(
                        "",
                        it.message ?: ""
                    )
                )
            }

        } else {

            BaseObservableViewModel.baseSubject.onNext(
                BaseEvents.ShowAlertDialogInMenu(
                    "",
                    R_string(R.string.message_error_print_device)
                )
            )
        }
    }

    fun chargeTicketReprint(it: ResponseChargeTicketModel) {


        val request = getRequestBalotoModel()

        val taxes = it.exchangeTicketDTO?.tax?.first()
        val headerBaloto = request.headerBaloto
        val transactionDate = DateUtil.convertStringToDate(
            DateUtil.StringFormat.EEE_DD_MM_YY,
            DateUtil.addBackslashToStringDate(it.transactionDate ?: "")
        ).toUpperCase() + " " + it.transactionHour
        val boards = it.exchangeTicketDTO?.board
        val totalValueWithOutRematch = (taxes?.grossAmount)
        val totalValueRematch = (taxes?.taxRate)
        val iva = (taxes?.taxAmount)
        val totalWithOutIva = taxes?.netAmount
        val total = ((taxes?.netAmount ?: 0) + (taxes?.taxAmount ?: 0))
        val drawInfo = getDrawsDate(
            BalotoSendBetModel().apply { drawId = it.exchangeTicketDTO?.drawId },
            betDuration
        )
        val serialNumber = it.serialNumber
        val checkDigit = it.checkDigit
        val wagerGuardNumber = it.wagerGuardNumber
        val footerTicket = request.footerTicket

        printBalotoTicketSale(
            headerBaloto,
            transactionDate,
            boards,
            "$${formatValue(totalValueWithOutRematch?.toDouble() ?: 0.0)}",
            "$${formatValue(totalValueRematch?.toDouble() ?: 0.0)}",
            "$${iva?.toDouble()?.let { it2 -> formatValue(it2) }}",
            "$${formatValue((totalWithOutIva ?: 0).toDouble())}",
            "$${formatValue(total.toDouble())}",
            drawInfo,
            serialNumber,
            checkDigit,
            wagerGuardNumber,
            footerTicket, true
        ) { itl ->
            if (itl == PrinterStatus.OK) {

                BaseObservableViewModel.baseSubject.onNext(
                    BaseEvents.ShowAlertDialogInMenu(
                        "",
                        it.message ?: ""
                    )
                )
            } else {

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

@SuppressLint("CheckResult")
private fun BalotoViewModel.getBalotoResults(reportString: String) {
    interactor.getBalotoResult(reportString)?.subscribe({
        if (it.isSuccess) {
            singleLiveEvent.value =
                it.balotoResults?.let { it1 -> BalotoViewModel.ViewEvent.GetBalotoResults(it1) }
        } else {
            singleLiveEvent.value = BalotoViewModel.ViewEvent.ResponseError(it.message.toString())
        }
    }, {
        if (it is ConnectException) {
            singleLiveEvent.value =
                BalotoViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
        } else if (it !is SocketTimeoutException) {
            singleLiveEvent.value =
                BalotoViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
        }
    })
}

@SuppressLint("CheckResult")
private fun BalotoViewModel.getBalotoParameters() {

    interactor.getBalotoParameters()?.subscribe({
        if (it.isSuccess ?: false) {

            processParameterBaloto(it)

        } else {
            singleLiveEvent.value = BalotoViewModel.ViewEvent.ShowMessage(
                it.message ?: R_string(R.string.message_error_transaction), false
            )
        }
    }, {
        if (it is ConnectException) {
            singleLiveEvent.value = BalotoViewModel.ViewEvent.ShowMessage(
                R_string(R.string.message_no_network),
                false
            )
        } else if (it !is SocketTimeoutException) {
            singleLiveEvent.value =
                BalotoViewModel.ViewEvent.ShowMessage(
                    R_string(R.string.message_error_transaction),
                    false
                )
        }
    })

}

private fun BalotoViewModel.processParameterBaloto(it: BalotoParametersModel) {

    parametersModel = it

    maxDuration = it.maxDuration ?: 9
    maxBoards = it.maxBoards ?: 5
    primarySelectionsLowNumber = it.primarySelectionsLowNumber ?: 1
    primarySelectionsHighNumber = it.primarySelectionsHighNumber ?: 43
    secondarySelectionsLowNumber = it.secondarySelectionsLowNumber ?: 1
    secondarySelectionsHighNumber = it.secondarySelectionsHighNumber ?: 16
    basePrice = it.basePrice ?: 5700

    boardListLiveData.value = mutableListOf(BalotoBoardModel().apply {
        selections = arrayOf("", "", "", "", "", "")
        stake = it.basePrice
        quickpick = false
        addonPlayed = false
        primarySelectionsLowNumber = it.primarySelectionsLowNumber
        primarySelectionsHighNumber = it.primarySelectionsHighNumber
        secondarySelectionsLowNumber = it.secondarySelectionsLowNumber
        secondarySelectionsHighNumber = it.secondarySelectionsHighNumber

    })

}

@SuppressLint("CheckResult")
private fun BalotoViewModel.getBalotoAccumulated() {
    interactor.getBalotoParameters()?.subscribe({
        if (it.isSuccess == true) {
            singleLiveEvent.value = it.draws?.let { it1 -> BalotoViewModel.ViewEvent.GetDraws(it1) }
        } else {
            singleLiveEvent.value =
                BalotoViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
        }
    }, {
        if (it is ConnectException) {
            singleLiveEvent.value =
                BalotoViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
        } else if (it !is SocketTimeoutException) {
            singleLiveEvent.value =
                BalotoViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
        }
    })
}

@SuppressLint("CheckResult")
private fun BalotoViewModel.checkeTicketState(ticketSerialNumber: String) {
    interactor.checkeTicketState(ticketSerialNumber)?.subscribe({
        if (it.isSuccess == true) {
            singleLiveEvent.value = BalotoViewModel.ViewEvent.ResponsTicketSerie(it)
        } else {
            singleLiveEvent.value = BalotoViewModel.ViewEvent.ResponseError(
                it.message ?: R_string(R.string.message_error_transaction)
            )
        }
    }, {
        if (it is ConnectException) {
            singleLiveEvent.value =
                BalotoViewModel.ViewEvent.ResponseError(R_string(R.string.message_no_network))
        } else if (it !is SocketTimeoutException) {
            singleLiveEvent.value =
                BalotoViewModel.ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
        }
    })
}

private fun BalotoViewModel.printBalotoTicketSale(
    headerBaloto: String?,
    transactionDate: String?,
    boards: List<BalotoBoardDTO>?,
    valueBaloto: String,
    valueRevenge: String,
    iva: String,
    withoutIvaTotal: String,
    total: String,
    drawInfo: String,
    serialNumber: String?,
    checkDigit: String?,
    wagerNumber: String?,
    footer: String?,
    isExchange: Boolean,
    callback: (printerStatus: PrinterStatus) -> Unit?
) {
    interactor.printBalotoSaleTicket(
        headerBaloto,
        transactionDate,
        boards,
        valueBaloto,
        valueRevenge,
        iva,
        withoutIvaTotal,
        total,
        drawInfo,
        serialNumber,
        checkDigit,
        wagerNumber,
        footer,
        isExchange,
        callback
    )
}

private fun compareBoardList(
    boardsDTO: List<BalotoBoardDTO>?,
    boardsRequest: List<BalotoBoardModel>
): List<BalotoBoardDTO>? {
    val boards = arrayListOf<BalotoBoardDTO>()
    boardsDTO?.forEachIndexed { index, balotoBoardDTO ->
        val board = BalotoBoardDTO().apply {
            quickpick = boardsRequest[index].quickpick
            stake = boardsRequest[index].stake
            selections = balotoBoardDTO.selections
            addonPlayed = boardsRequest[index].addonPlayed
        }
        boards.add(board)
    }
    return boards.toList()
}

/*
*
* Este metodo se debe usar al momento de imprimir
* **/

private fun getDrawsDate(balotoSendBetModel: BalotoSendBetModel, duration: Int): String {
    val drawsList = balotoSendBetModel.drawId?.split(",") ?: arrayListOf()
    var footer =
        R_string(R.string.print_date_draws_baloto) + duration + ":" + R_string(R.string.print_date_draws_baloto_s) + drawsList[0] + " " + formatDrawDate(
            drawsList[1]
        ) + " " + drawsList[2] + "; "
    val draws = StringBuilder()
    if (drawsList.size > 3) {
        var index = 3
        while (index < drawsList.size) {
            draws.append(
                R_string(R.string.print_date_draws_baloto_s) + drawsList[index] + " " + formatDrawDate(
                    drawsList[index + 1]
                ) + "; "
            )
            index += 2
        }
        footer = footer + draws.toString()
    }
    return footer
}

private fun formatDrawDate(id: String): String {
    val c = Calendar.getInstance()
    c.set(Calendar.DAY_OF_MONTH, 31)
    c.set(Calendar.MONTH, 11)
    c.set(Calendar.YEAR, 1999)
    c.set(Calendar.HOUR, 0)
    c.set(Calendar.MINUTE, 0)
    c.set(Calendar.SECOND, 0)
    c.set(Calendar.MILLISECOND, 0)
    c.add(Calendar.DAY_OF_MONTH, Integer.valueOf(id) - 1)
    val dateFirstDraw = c.time
    return DateUtil.convertDateToStringFormat(DateUtil.StringFormat.DMMMYY.rawValue, dateFirstDraw)
        .toUpperCase()
}
