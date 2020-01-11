package co.com.pagatodo.core.data.print

import android.graphics.Paint
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.print.PrintManager.Companion.addLineBreak
import co.com.pagatodo.core.data.print.PrintManager.Companion.addLineBreakHeaderPaper
import co.com.pagatodo.core.data.print.PrintManager.Companion.setFontStyle
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.formatValue
import com.pos.device.printer.PrintCanvas

class RechargePrintNEW9220 : BasePrint(), IRechargePrint {

    override fun printRecharge(
        stub: String,
        billNumber: String,
        transactionDate: String,
        transactionHour: String,
        number: String,
        value: String,
        billPrefix: String,
        billMessage: String,
        isRetry: Boolean,
        headerGelsa:String,
        decriptionPackage:String,
        operationName :String,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        val sellerCode = getPreference<String>(R_string(R.string.shared_key_seller_code))
        val municipalityCode = getPreference<String>(R_string(R.string.shared_key_municipality_code))
        val officeCode = getPreference<String>(R_string(R.string.shared_key_office_code))
        val imei = getDeviceId()

        val canvas = PrintCanvas()
        val paint = Paint()
        addLineBreakHeaderPaper(canvas, paint)
        setFontStyle(paint, 2, true)

        canvas.drawText("$sellerCode $municipalityCode $officeCode $imei", paint)
        canvas.drawText("Colilla: $stub", paint)
        canvas.drawText("No Factura: $billPrefix-$billNumber", paint)
        canvas.drawText("Fecha: $transactionDate", paint)
        canvas.drawText("Hora: $transactionHour", paint)
        canvas.drawText("Línea Recargada: $number", paint)
        canvas.drawText("Valor Recarga: $${formatValue(value.toDouble())}", paint)
        canvas.drawText("-$billPrefix", paint)

        addLineBreak(canvas, paint)

        if (billMessage.isNotEmpty()) {
            canvas.drawText(billMessage, paint)
        } else {
            addLineBreak(canvas, paint)
        }

        addLineBreak(canvas, paint, 13)
        PrintManager.print(canvas, callback)
    }

    override fun printBetplay(
        documentId: String,
        value: Int,
        sellerCode: String,
        digitChecking: String,
        stubs: String,
        isRetry: Boolean,
        typePrintText: String,
        isReprint: Boolean,
        isCollect: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        val canvas = PrintCanvas()
        val paint = Paint()
        addLineBreakHeaderPaper(canvas, paint)

        setFontStyle(paint, 2, false)
        canvas.drawText("${getCurrentDate()}", paint)
        addLineBreak(canvas, paint, 2)
        canvas.drawText("${R_string(R.string.print_seller_equal_upper)} $sellerCode     M: ${getDeviceId()}", paint)
        canvas.drawText(
            "${R_string(R.string.print_stub_equal)}  $stubs${if (isRetry) {
                R_string(R.string.print_retry_icon)
            } else {
                ""
            }}", paint
        )
        canvas.drawText("${R_string(R.string.print_check_equal)} $digitChecking", paint)
        addLineBreak(canvas, paint, 2)
        canvas.drawText("${R_string(R.string.print_betplay_recharge)}", paint)
        addLineBreak(canvas, paint, 2)
        canvas.drawText("${R_string(R.string.print_document_text)}        : $documentId", paint)
        canvas.drawText("${R_string(R.string.print_recharge_value)}     : $value", paint)

        addLineBreak(canvas, paint, 10)
        PrintManager.print(canvas, callback)
    }
}