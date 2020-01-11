package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.print.VirtualSoatPrintModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.print.POSPrinter
import java.lang.Exception
import java.util.*

class VirtualSoatPrint: BasePrint(), IVirtualSoatPrint {

    private var posPrinter = POSPrinter()

    override fun print(printModel: VirtualSoatPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?) {
        
        try {
            posPrinter.printWithAlignment(generatePrintSoat(printModel), arrayOf(1,2), callback)
        }catch (e: Exception) {
            callback(PrinterStatus.ERROR)
        }
    }

}