package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.print.VirtualSoatPrintModel

class VirtualSoatPrint: BasePrint(), IVirtualSoatPrint {
    
    override fun print(printModel: VirtualSoatPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?) {
        // not implemented
    }

}