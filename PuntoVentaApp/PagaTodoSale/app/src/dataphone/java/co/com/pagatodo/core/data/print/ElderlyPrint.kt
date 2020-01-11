package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.print.ElderlyPrintModel

class ElderlyPrint:BasePrint(),IElderlyPrint {

    override fun printPayment(elderlyPrintModel: ElderlyPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?) {
        // not implemented
    }


}