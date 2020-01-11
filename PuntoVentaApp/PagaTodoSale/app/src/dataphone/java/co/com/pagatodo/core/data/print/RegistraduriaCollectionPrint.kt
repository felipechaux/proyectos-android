package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.print.RegistraduriaCollectionPrintModel

class RegistraduriaCollectionPrint : BasePrint(), IRegistraduriaCollectionPrint {


    override fun registraduriaCollectionPrint(
        printModel: RegistraduriaCollectionPrintModel,
        callback: (printerStatus: PrinterStatus) -> Unit?) {

        // Not implemented
    }

    override fun registraduriaCollectionReprint(
        printModel: RegistraduriaCollectionPrintModel,
        callback: (printerStatus: PrinterStatus) -> Unit?) {

        // Not implemented
    }

}