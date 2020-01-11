package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.print.PayBillPrintModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.util.print.POSPrinter

class PayBillPrint: BasePrint(), IPayBillPrint {

    val stringBuilder = StringBuilder()
    var posPrinter = POSPrinter()

    override fun print(
        printModel: PayBillPrintModel,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        stringBuilder.clear()

        stringBuilder.append("\nRECIBO RECAUDO EMPRESARIAL")
        stringBuilder.append("\nCOLABORADOR EMPRESARIAL")

        val arrayHeader = printModel.header?.split(";")
        arrayHeader?.forEach {
            stringBuilder.append("\n$it")
        }

        stringBuilder.append("\n\nPDV:${getPreference<String>(R_string(R.string.shared_key_office_code))}")
        stringBuilder.append("\nAsesor:${getPreference<String>(R_string(R.string.shared_key_seller_code))}")
        stringBuilder.append("\nFecha: ${printModel.date} Hora: ${printModel.hour}")
        stringBuilder.append("\nNUMERO COMPROBANTE: ${printModel.voucherNumber}")
        stringBuilder.append("\nNUMERO FACTURA: ${printModel.billNumber}")
        stringBuilder.append("\nNUMERO REFERENCIA: ${printModel.referenceCode}")
        stringBuilder.append("\nCODIGO: ${printModel.code}")

        stringBuilder.append("\n\nDescripcion Servicio: ${printModel.descriptionService ?: ""}")
        stringBuilder.append("\nValor del pago: $${formatValue(printModel.valueToPay ?: 0.0)}")
        stringBuilder.append("\nTOTAL: $${formatValue(printModel.totalValue ?: 0.0)}")

        val linePositionsToCenter = arrayOf(1,2,3,4,5,6)
        posPrinter.printWithAlignment(stringBuilder.toString(), linePositionsToCenter, callback)
    }
}