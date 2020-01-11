package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.dto.request.RequestBalotoCheckTicketDTO
import co.com.pagatodo.core.data.dto.request.RequestBalotoParametersDTO
import co.com.pagatodo.core.data.dto.request.RequestBalotoSendBetDTO
import co.com.pagatodo.core.data.dto.request.RequestChargeTicketDTO
import co.com.pagatodo.core.data.dto.response.*
import co.com.pagatodo.core.data.model.BalotoParametersModel
import co.com.pagatodo.core.data.model.print.BalotoPrintModel
import co.com.pagatodo.core.data.print.BalotoPrint
import co.com.pagatodo.core.data.print.PrinterStatus
import io.reactivex.Observable

interface IBalotoRepository {

    fun checkStateTicket(requestBalotoCheckTicketDTO: RequestBalotoCheckTicketDTO): Observable<ResponseBalotoCheckTicketDTO>?
    fun getBalotoResult(requestBalotoParametersDTO: RequestBalotoParametersDTO): Observable<ResponseBalotoResultsDTO>?
    fun getBalotoParameters(requestBalotoParametersDTO: RequestBalotoParametersDTO): Observable<ResponseBalotoParametersDTO>?
    fun sendBalotoBet(requestBalotoSendBetDTO: RequestBalotoSendBetDTO, isRetry: Boolean = true, transactionType: String? = null): Observable<ResponseBalotoSendBetDTO>?
    fun chargeTicket(requestChargeTicketDTO: RequestChargeTicketDTO): Observable<ResponseChargeTicketDTO>?

    fun printBalotoSaleTicket(balotoPrintModel:BalotoPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?)

    fun printBalotoQueyTicket(
        transactionDate: String, numberTicket: String, valuePrize: String, tax: String, valuePay: String, terminalCode: String, salePointCode: String,
        authorizerNumber: String, stub: String, sellerCode: String, callback: (printerStatus: PrinterStatus) -> Unit?
    )

    fun saveLocalParameterBaloto(balotoParametersModel: BalotoParametersModel)
    fun getLocalParameterBaloto(): BalotoParametersModel


}