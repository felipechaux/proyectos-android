package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.print.PayuCollectingPrintModel

interface IPayuPrint {
    fun print(printModel: PayuCollectingPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
}