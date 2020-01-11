package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.print.ColpensionesBepsPrintModel

interface IColpensionesBepsPrint {
    fun print(printModel:ColpensionesBepsPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?)
}