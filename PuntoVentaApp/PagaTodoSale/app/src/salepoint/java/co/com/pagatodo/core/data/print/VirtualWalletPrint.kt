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
        var indexItem = 1
        stringBuilder.clear()
        val arrayCenterItems = mutableListOf<Int>()
        arrayCenterItems.add(indexItem)

        val header = printModel.textHeader?.split("\\n")
        val body = printModel.textBody?.split("\\n")

        header?.forEachIndexed { _, item ->
            stringBuilder.append("\n$item")
            indexItem += 1
            arrayCenterItems.add(indexItem)
        }

        stringBuilder.append("\n")
        body?.forEachIndexed { _, item ->
            stringBuilder.append("\n$item")
            indexItem += 1
            arrayCenterItems.add(indexItem)
        }

        stringBuilder.append("\n\n\nPin: ${printModel.pin}")
        stringBuilder.append("\nUsuario: ${getPreference<String>(R_string(R.string.shared_key_seller_code))}")
        stringBuilder.append("\nValor Pin: ${printModel.pinValue}")

        try {
            posPrinter.printWithAlignment(stringBuilder.toString(), arrayCenterItems.toTypedArray(), callback)
        }catch (e: Exception) {
            callback(PrinterStatus.ERROR)
        }
    }

}