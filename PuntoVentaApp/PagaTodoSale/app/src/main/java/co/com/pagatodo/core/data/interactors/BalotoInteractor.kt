package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.AwarBalotoDTO
import co.com.pagatodo.core.data.dto.BalotoBoardDTO
import co.com.pagatodo.core.data.dto.BalotoResultDTO
import co.com.pagatodo.core.data.dto.request.RequestBalotoCheckTicketDTO
import co.com.pagatodo.core.data.dto.request.RequestBalotoParametersDTO
import co.com.pagatodo.core.data.dto.request.RequestBalotoSendBetDTO
import co.com.pagatodo.core.data.dto.request.RequestChargeTicketDTO
import co.com.pagatodo.core.data.dto.response.ProductParametersDTO
import co.com.pagatodo.core.data.dto.response.ResponseBalotoParametersDTO
import co.com.pagatodo.core.data.model.*
import co.com.pagatodo.core.data.model.print.BalotoPrintModel
import co.com.pagatodo.core.data.model.response.ResponseChargeTicketModel
import co.com.pagatodo.core.data.model.response.ResponseBalotoCheckTicketModel
import co.com.pagatodo.core.data.model.response.ResponseBalotoResultsModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.repositories.IBalotoRepository
import co.com.pagatodo.core.di.BalotoModule
import co.com.pagatodo.core.di.DaggerBalotoComponent
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.StorageUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.StringBuilder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BalotoInteractor {
    @Inject
    lateinit var balotoRepository: IBalotoRepository

    init {
        DaggerBalotoComponent.builder().balotoModule(BalotoModule()).build().inject(this)
    }

    fun getBalotoResult(reportString: String): Observable<ResponseBalotoResultsModel>? {
        val request = createRequestBalotoParametersDTO(reportString)
        return balotoRepository.getBalotoResult(request)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = ResponseBalotoResultsModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionHour
                    message = it.message
                    balotoResults = convertBalotoResultsDtoToModel(it.balotoResults)
                }
                Observable.just(response)
            }
    }

    private fun convertBalotoResultsDtoToModel(list: List<BalotoResultDTO>?): MutableList<BalotoResultsModel> {
        val items = mutableListOf<BalotoResultsModel>()
        list?.forEach {
            val balotoResult = BalotoResultsModel().apply {
                balotoNumber = it.balotoNumber
                revengeNumber = it.revengeNumber
                draw = it.draw
                date = it.date
                type = it.type
                awards = it.awards?.let { it1 -> convertAwardsDtoToListTriple(it1) }
            }
            items.add(balotoResult)
        }
        return items
    }

    private fun convertAwardsDtoToListTriple(awards: List<AwarBalotoDTO>): List<Triple<String, String, String>> {
        val awardsTriple: MutableList<Triple<String, String, String>> = arrayListOf()
        awards.forEach {
            awardsTriple.add(Triple(it.div, it.quantity, it.winners))
        }
        return awardsTriple
    }

    private fun createRequestBalotoParametersDTO(reportString: String): RequestBalotoParametersDTO {
        return RequestBalotoParametersDTO().apply {
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            productCode = R_string(R.string.code_product_baloto)
            userType = getPreference(R_string(R.string.shared_key_user_type))
            report = reportString
        }
    }

    fun getBalotoParameters(): Observable<BalotoParametersModel>? {

        val requestBalotoParametersDTO = RequestBalotoParametersDTO().apply {
            productCode = R_string(R.string.code_product_baloto)
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            userType = getPreference(R_string(R.string.shared_key_user_type))
        }

        return balotoRepository.getBalotoParameters(requestBalotoParametersDTO)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = BalotoParametersModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionTime
                    transactionId = it.transactionId
                    message = it.message
                    messages = it.messages
                    status = it.status
                    basePrice = it.basePrice
                    maxPrice = it.maxPrice
                    minBoards = it.minBoards
                    maxBoards = it.maxBoards
                    quickPickAvailable = it.quickPickAvailable
                    multiplierAvailable = it.multiplierAvailable
                    stakes = it.stakes
                    durations = it.durations
                    maxDuration = it.maxDuration
                    primarySelectionsLowNumber = it.primarySelectionsLowNumber
                    primarySelectionsHighNumber = it.primarySelectionsHighNumber
                    secondarySelectionsLowNumber = it.secondarySelectionsLowNumber
                    secondarySelectionsHighNumber = it.secondarySelectionsHighNumber
                    gameId = it.gameId
                    revision = it.revision
                    addonGames = it.addonGames
                    draws = it.draws
                    productParameters = convertDtoToModel(it.productParameters)
                }
                Observable.just(response)
            }
    }

    private fun convertDtoToModel(dto: ProductParametersDTO?): ProductParametersModel {
        return ProductParametersModel().apply {
            headerTicketSp = dto?.headerTicketSp
            footerTicketSp = dto?.footerTicketSp
            headerTicketPv = dto?.headerTicketPv
            footerTicketPv = dto?.footerTicketPv
        }
    }

    fun sendBalotoBet(
        totalValue: Int,
        totalDraws: Int,
        boardList: List<BalotoBoardModel>,
        isRetry: Boolean = true,
        transactionType: String? = null,
        transactionTime: Long?,
        sequenceTransaction: Int?
    ): Observable<BalotoSendBetModel>? {

        val listBoardsDTO: MutableList<BalotoBoardDTO> = arrayListOf()

        boardList.forEach {
            if (it.quickpick == true) {
                it.selections = null
            }

            listBoardsDTO.add(BalotoBoardDTO().apply {
                quickpick = it.quickpick
                stake = it.stake
                selections = it.selections?.toList()
                addonPlayed = it.addonPlayed
            })
        }

        val requestDTO = RequestBalotoSendBetDTO().apply {
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            productCode = R_string(R.string.code_product_baloto)
            userType = getPreference(R_string(R.string.shared_key_user_type))
            serie1 = getPreference(R_string(R.string.shared_key_current_serie1))
            serie2 = getPreference(R_string(R.string.shared_key_current_serie2))
            gameName = R_string(R.string.baloto_game_name)
            value = totalValue
            duration = totalDraws
            price = totalValue
            boardDTOS = listBoardsDTO.toList()
            this.transactionTime = transactionTime
            this.sequenceTransaction = sequenceTransaction
        }

        return balotoRepository.sendBalotoBet(requestDTO, isRetry, transactionType)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = BalotoSendBetModel().apply {
                    isSuccess = it.isSuccess
                    serie1 = it.serie1
                    serie2 = it.serie2
                    currentSerie2 = it.currentSerie2
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionHour
                    terminalId = it.terminalId
                    uniqueSerial = it.uniqueSerial
                    drawId = it.drawId
                    gameName = it.gameName
                    serialNumber = it.serialNumber
                    board = it.board
                    drawStartDate = it.drawStartDate
                    drawEndDate = it.drawEndDate
                    ticketSecurity = it.ticketSecurity
                    tax = it.tax
                    wagerGuardNumber = it.wagerGuardNumber
                    checkDigit = it.checkDigit
                }
                Observable.just(response)
            }
    }

    fun chargeTicket(
        numberSerialTicket: String,
        canPay: Boolean
    ): Observable<ResponseChargeTicketModel>? {
        val request = createRequestBalotoChargeTicketDTO(numberSerialTicket, canPay)
        return balotoRepository.chargeTicket(request)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = ResponseChargeTicketModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess ?: false
                    message = it.message
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionHour
                    transactionId = it.transactionId
                    uniqueSerial = it.uniqueSerial
                    status = it.status
                    rejectReason = it.rejectReason
                    serialNumber = it.serialNumber
                    transactionTime = it.transactionTime
                    terminalId = it.terminalId
                    ticketStatus = it.ticketStatus
                    cashAmount = it.cashAmount
                    taxes = it.taxes
                    netAmount = it.netAmount
                    serie1 = it.serie1
                    serie2 = it.serie2
                    currentSerie2 = it.currentSerie2
                    checkDigit = it.checkDigit
                    exchangeTicketDTO = it.exchangeTicket
                    wagerGuardNumber = it.wagerGuardNumber
                }
                Observable.just(response)
            }
    }

    fun checkeTicketState(ticketSerialNumber: String): Observable<ResponseBalotoCheckTicketModel>? {
        val request = createRequestBalotoCheckTicketDTO(ticketSerialNumber)
        return balotoRepository.checkStateTicket(request)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = ResponseBalotoCheckTicketModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    message = it.message
                    status = it.status
                    canPay = it.canPay
                    ticketStatus = it.ticketStatus
                    cashAmount = it.cashAmount
                    netAmount = it.netAmount
                    serialNumber = it.serialNumber
                    rejectReason = it.rejectReason
                    transactionTime = it.transactionTime
                }
                Observable.just(response)
            }
    }

    private fun createRequestBalotoCheckTicketDTO(ticketSerialNumber: String): RequestBalotoCheckTicketDTO {
        return RequestBalotoCheckTicketDTO().apply {
            canalId = R_string(R.string.channel_id_product_baloto)
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            userType = getPreference(R_string(R.string.shared_key_user_type))
            serie1 = getPreference(R_string(R.string.shared_key_current_serie1))
            serie2 = getPreference(R_string(R.string.shared_key_current_serie2))
            productCode = R_string(R.string.code_product_baloto)
            this.ticketSerialNumber = ticketSerialNumber
        }
    }

    private fun createRequestBalotoChargeTicketDTO(
        numberSerialTicket: String,
        canPay: Boolean
    ): RequestChargeTicketDTO {
        return RequestChargeTicketDTO().apply {
            channelId = R_string(R.string.channel_id_product_baloto)
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            userType = getPreference(R_string(R.string.shared_key_user_type))
            serie1 = getPreference(R_string(R.string.shared_key_current_serie1))
            serie2 = getPreference(R_string(R.string.shared_key_current_serie2))
            productCode = R_string(R.string.code_product_baloto)
            this.numberSerialTicket = numberSerialTicket
            this.canPay = canPay
            transactionTime = DateUtil.getCurrentDateInUnix()
        }
    }

    fun printBalotoSaleTicket(
        headerBaloto: String?,
        transactionDate: String?,
        boards: List<BalotoBoardDTO>?,
        valueBaloto: String,
        valueRevenge: String,
        iva: String,
        totalWithoutIva: String,
        total: String,
        drawInfo: String,
        serialNumber: String?,
        checkDigit: String?,
        wagerNumber: String?,
        footer: String?,
        isExchange: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        val numbers = convertBoardsToStringList(boards)
        val serie1 = getPreference<String>(R_string(R.string.shared_key_current_serie1))
        val serie2 = getPreference<String>(R_string(R.string.shared_key_current_serie2))
        val terminal = getPreference<String>(R_string(R.string.shared_key_terminal_code))
        val stubs = "$serie1 $serie2"
        val sellerName = getPreference<String>(R_string(R.string.shared_key_seller_name))
        val sellerCode = getPreference<String>(R_string(R.string.shared_key_seller_code))

        val balotoPrintModel = BalotoPrintModel().apply {
            this.headerBaloto = headerBaloto
            this.transactionDate = transactionDate
            this.numbers = numbers
            this.valueBaloto = valueBaloto
            this.valueRevenge = valueRevenge
            this.iva = iva
            this.withoutIvaTotal = totalWithoutIva
            this.total = total
            this.drawInfo = drawInfo
            this.serialNumber = serialNumber
            this.sellerName = sellerName
            this.terminal = terminal
            this.sellerCode = sellerCode
            this.stubs = stubs
            this.checkNumber = checkDigit
            this.transactionNumber = wagerNumber
            this.footerBaloto = footer
            this.isExchange = isExchange
        }
        balotoRepository.printBalotoSaleTicket(balotoPrintModel, callback)
    }

    private fun convertBoardsToStringList(boards: List<BalotoBoardDTO>?): List<String> {
        val numbers = mutableListOf<String>()
        boards?.forEachIndexed { index, it ->
            val sentece = StringBuilder()
            val numOfSpacesBetween = if (DeviceUtil.isSalePoint()) 3 else 2
            val numOfSpacesAfterNumber = if (DeviceUtil.isSalePoint()) 3 else 1
            var numOfSpacesBeforeSuper = if (DeviceUtil.isSalePoint()) 7 else 4
            numOfSpacesBeforeSuper -= numOfSpacesAfterNumber
            val typeBaloto = if (it.addonPlayed == true) "BR" else "B "
            val automatic = if (it.quickpick == true) "AU" else ""
            val letter = convertNumberIndexToLetter(index)
            it.selections?.forEachIndexed { index, itl ->
                if (index >= 5) {
                    if (DeviceUtil.isSalePoint())
                        sentece.append(" ".repeat(numOfSpacesBeforeSuper).plus("$itl"))
                    else
                        sentece.append("-$itl")
                } else {
                    sentece.append("${itl.plus(" ".repeat(numOfSpacesAfterNumber))}")
                }
            }
            numbers.add(
                "${letter.plus(".").plus(" ".repeat(numOfSpacesBetween))}${typeBaloto.plus(
                    " ".repeat(
                        numOfSpacesBetween
                    )
                )}${sentece.trim().toString().plus(" ".repeat(numOfSpacesBetween))}${automatic}"
            )
        }
        return numbers
    }

    private fun convertNumberIndexToLetter(index: Int): String {
        var letter = ""
        when (index) {
            0 -> letter = "A"
            1 -> letter = "B"
            2 -> letter = "C"
            3 -> letter = "D"
            4 -> letter = "E"
        }
        return letter
    }

    fun printBalotoQueryTicket(
        transacationDate: String,
        numberTicket: String,
        valuePrize: String,
        tax: String,
        valuePay: String,
        authorizerNumber: String,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        val serie1 = getPreference<String>(R_string(R.string.shared_key_current_serie1))
        val serie2 = getPreference<String>(R_string(R.string.shared_key_current_serie2))
        val terminal = getPreference<String>(R_string(R.string.shared_key_terminal_code))
        val stubs = "$serie1 $serie2"
        val sellerCode = getPreference<String>(R_string(R.string.shared_key_seller_code))

        balotoRepository.printBalotoQueyTicket(
            transacationDate,
            numberTicket,
            valuePrize,
            tax,
            valuePay,
            terminal,
            terminal,
            authorizerNumber,
            stubs,
            sellerCode,
            callback
        )
    }

    
    fun saveLocalParameterBaloto(balotoParametersModel: BalotoParametersModel) {
        balotoRepository.saveLocalParameterBaloto(balotoParametersModel)
    }

    fun getLocalParameterBaloto(): BalotoParametersModel {
        return balotoRepository.getLocalParameterBaloto()
    }
}