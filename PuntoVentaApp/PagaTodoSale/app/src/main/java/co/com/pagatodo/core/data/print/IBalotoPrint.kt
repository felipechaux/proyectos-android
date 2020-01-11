package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.print.BalotoPrintModel

interface IBalotoPrint {
    fun printBalotoSaleTicket(balotoPrintModel:BalotoPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?)
    fun printBalotoQueryTicket(
        transactionDate: String ,numberTicket: String, valuePrize: String, tax: String, valuePay: String, terminalCode: String, salePointCode: String,
        authorizerNumber: String, stub: String, sellerCode: String, callback: (printerStatus: PrinterStatus) -> Unit?
    )
}