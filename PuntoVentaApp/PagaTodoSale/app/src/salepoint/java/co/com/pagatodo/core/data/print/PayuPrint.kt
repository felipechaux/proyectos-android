package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.print.PayuCollectingPrintModel
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.util.print.POSPrinter

class PayuPrint: BasePrint(), IPayuPrint {

    val stringBuilder = StringBuilder()
    var posPrinter = POSPrinter()

    override fun print(printModel: PayuCollectingPrintModel, isReprint: Boolean,
                       callback: (printerStatus: PrinterStatus) -> Unit?) {
        stringBuilder.clear()

        stringBuilder.append("\nGRUPO EMPRESARIAL EN LINEA")
        stringBuilder.append("\nNIT 030111257-3")
        stringBuilder.append("\nTRANSACCION EXITOSA")

        stringBuilder.append("\n\nCONVENIO: PAYU")

        stringBuilder.append("\n\nNO TRANSACCION: ${printModel.transactionNumber}")
        stringBuilder.append("\n\nNO COMPROBANTE: ${printModel.voucherNumber}")

        stringBuilder.append("\n\nREFERENCIA DE PAGO: ${printModel.paymentReference}")
        stringBuilder.append("\nIDENTIFICACION: ${printModel.userDocument}")
        stringBuilder.append("\nNOMBRES: ${printModel.userNames}")
        stringBuilder.append("\nMENSAJE: ${printModel.message}")

        stringBuilder.append("\n\nVALOR RECAUDADO: ${formatValue(printModel.collectedValue!!.toDouble()) }")
        stringBuilder.append("\n\n${printModel.transactionDate}")
        stringBuilder.append("\n\n${printModel.textFooter}")
        stringBuilder.append("\nTERMINAL: ${printModel.terminalCode}")

        if (!isReprint)
            stringBuilder.append("\n\n-ORIGINAL-")
        else
            stringBuilder.append("\n\n-REIMPRESIÓN-")

        val linePositionsToCenter = arrayOf(1,2,3)
        posPrinter.printWithAlignment(stringBuilder.toString(), linePositionsToCenter, callback)
    }
}