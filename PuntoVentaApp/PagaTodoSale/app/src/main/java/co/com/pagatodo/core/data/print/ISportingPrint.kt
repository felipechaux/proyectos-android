package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.print.SportingPrintModel

interface ISportingPrint {
    fun print(printModel: SportingPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
}