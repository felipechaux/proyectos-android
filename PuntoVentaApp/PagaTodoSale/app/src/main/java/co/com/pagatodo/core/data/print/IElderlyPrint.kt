package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.print.ElderlyPrintModel

interface IElderlyPrint {
    fun printPayment(elderlyPrintModel: ElderlyPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?)
}