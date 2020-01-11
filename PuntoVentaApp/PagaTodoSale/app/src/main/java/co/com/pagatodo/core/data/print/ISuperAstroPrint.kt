package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.print.SuperAstroPrintModel

interface ISuperAstroPrint {
    fun print(printModel: SuperAstroPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
}