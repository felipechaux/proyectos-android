package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.print.RegistraduriaCollectionPrintModel
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.print.POSPrinter
import java.lang.Exception

class RegistraduriaCollectionPrint: BasePrint(), IRegistraduriaCollectionPrint {

    val stringBuilder = StringBuilder()
    var posPrinter = POSPrinter()

    override fun registraduriaCollectionPrint(
        printModel: RegistraduriaCollectionPrintModel,
        callback: (printerStatus: PrinterStatus) -> Unit? ) {

        // Not Implemented
    }

    override fun registraduriaCollectionReprint(printModel: RegistraduriaCollectionPrintModel,
                                                callback: (printerStatus: PrinterStatus) -> Unit?) {

        // Not implemented
    }

}