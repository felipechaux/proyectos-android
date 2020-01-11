package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.print.ColpensionesBepsPrintModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.util.print.POSPrinter

class ColpensionesBepsPrint: BasePrint(), IColpensionesBepsPrint {
    private val stringBuilder = StringBuilder()
    private var posPrinter = POSPrinter()

    override fun print(printModel: ColpensionesBepsPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?) {
        stringBuilder.clear()

        val header = printModel.header?.split("\\n") ?: arrayListOf()
        stringBuilder.append("\n${header[0]}")
        stringBuilder.append("\n${header[1]}")

        if (printModel.isRejected){
            stringBuilder.append("\n${R_string(R.string.text_print_rejected_transaccion)}")
        }else{
            stringBuilder.append("\n${R_string(R.string.text_print_success_transaccion)}")
        }

        stringBuilder.append("\n\nConvenio: Colpensiones BEPS")

        if (!printModel.isRejected){
            stringBuilder.append("\n\nNO TRANSACCION: ${printModel.voucherNumber}")
            stringBuilder.append("\nNO AUTORIZACION: ${printModel.authNumber}")
            stringBuilder.append("\nNO COMPROBANTE: ${printModel.voucherNumber}")

            stringBuilder.append("\n\nIDENTIFICACION: ${printModel.userId}")
            stringBuilder.append("\nNOMBRE VINCULADO: ${printModel.userName}")
            stringBuilder.append("\n\nVALOR: $${formatValue(printModel.value?.toDouble() ?: 0.0)}")
        }

        val date = DateUtil.convertStringToDate(DateUtil.StringFormat.EEE_DD_MM_YYYY, DateUtil.addBackslashToStringDate(printModel.transactionDate ?: ""))
        stringBuilder.append("\n\n${date}")
        stringBuilder.append("\n${printModel.transactionHour}")

        if (printModel.isRejected){
            stringBuilder.append("\n${R_string(R.string.text_print_reason_rejection)}")
            stringBuilder.append("\n\n${printModel.rejectionReason}")
        }

        stringBuilder.append("\n\n${printModel.printText}")
        stringBuilder.append("\n\nUSUARIO: ${printModel.sellerCode}")
        stringBuilder.append("\nCODIGO ARRIENDO: ${printModel.rentCode}")
        stringBuilder.append("\nTERMINAL: ${printModel.terminal}")

        if (printModel.isReprint){
            stringBuilder.append("\n\n                ${R_string(R.string.text_print_reprint)}")
        }else{
            stringBuilder.append("\n\n               ${R_string(R.string.text_print_original)}")
        }

        try {
            posPrinter.printWithAlignment(stringBuilder.toString(), arrayOf(1,2,3), callback)
        }catch (e: Exception) {
            callback(PrinterStatus.ERROR)
        }
    }

}