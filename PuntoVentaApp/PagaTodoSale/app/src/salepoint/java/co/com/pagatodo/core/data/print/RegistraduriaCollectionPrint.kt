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
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        stringBuilder.clear()
        val arrayCenterItems = mutableListOf<Int>()

        stringBuilder.append("${printModel.textHeader}\n")
        stringBuilder.append("${printModel.nitInfo}\n\n")
        stringBuilder.append("Oficina: ${printModel.office}\n")
        stringBuilder.append("Codigo Arriendo: ${printModel.rentCode}\n")
        stringBuilder.append("Codigo Vendedor: ${printModel.sellerCode}\n\n")
        stringBuilder.append("${printModel.billTitle}\n\n")
        stringBuilder.append("Fecha y Hora: ${printModel.currentDate}\n")
        stringBuilder.append("PIN: ${printModel.pin}\n")
        stringBuilder.append("VALOR $: ${printModel.value}\n\n")
        stringBuilder.append("${printModel.secondTitle}\n\n")
        stringBuilder.append("Numero Documento: ${printModel.documentNumber}\n")
        stringBuilder.append("Nombres: ${printModel.names.toString().toUpperCase()}\n")
        stringBuilder.append("Apellidos: ${printModel.lastNames.toString().toUpperCase()}\n\n")
        stringBuilder.append("${printModel.textFooter}\n\n")
        stringBuilder.append("${printModel.originalBill}")

        arrayCenterItems.add(0)
        arrayCenterItems.add(1)
        arrayCenterItems.add(7)
        arrayCenterItems.add(13)
        arrayCenterItems.add(19)
        arrayCenterItems.add(21)

        try {
            posPrinter.printWithAlignment(stringBuilder.toString(), arrayCenterItems.toTypedArray(), callback)
        }catch (e: Exception) {
            callback(PrinterStatus.ERROR)
        }

    }

    override fun registraduriaCollectionReprint(printModel: RegistraduriaCollectionPrintModel,
                                                callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        // Not implemented
    }

}