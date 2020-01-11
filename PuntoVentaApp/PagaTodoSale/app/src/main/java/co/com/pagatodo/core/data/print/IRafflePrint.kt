package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.print.RafflePrintModel

interface IRafflePrint {
    fun print(printModel: RafflePrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
}