package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.print.PayBillPrintModel

interface IPayBillPrint {
    fun print(printModel: PayBillPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
}