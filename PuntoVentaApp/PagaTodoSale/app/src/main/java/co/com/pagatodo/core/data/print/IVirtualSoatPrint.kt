package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.print.VirtualSoatPrintModel

interface IVirtualSoatPrint {
    fun print(printModel: VirtualSoatPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?)
}