package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.print.VirtualWalletPrintModel

interface IVirtualWalletPrint {
    fun print(printModel: VirtualWalletPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?)
}