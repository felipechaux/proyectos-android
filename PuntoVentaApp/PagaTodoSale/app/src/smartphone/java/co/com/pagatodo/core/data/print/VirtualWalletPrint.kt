package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.print.VirtualWalletPrintModel
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.print.POSPrinter
import java.lang.Exception

class VirtualWalletPrint: BasePrint(), IVirtualWalletPrint {

    val stringBuilder = StringBuilder()
    var posPrinter = POSPrinter()

    override fun print(
        printModel: VirtualWalletPrintModel,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        // not implemented
    }

}