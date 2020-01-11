package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.print.RegistraduriaCollectionPrintModel

interface IRegistraduriaCollectionPrint {
    fun registraduriaCollectionPrint(printModel: RegistraduriaCollectionPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?)
    fun registraduriaCollectionReprint(printModel: RegistraduriaCollectionPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?)
}