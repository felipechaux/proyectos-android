package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.print.BbvaBillPayPrintModel

interface IBbvaPrint{
    fun print(bbvaBillPayPrintModel: BbvaBillPayPrintModel, callback: (printerStatus: PrinterStatus)-> Unit?)
}
class BbvaPrint: IBbvaPrint {
    override fun print(
        bbvaBillPayPrintModel: BbvaBillPayPrintModel,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

    }

}