package co.com.pagatodo.core.data.print

import android.graphics.Paint
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.GirorCheckRequestsDTO
import co.com.pagatodo.core.util.RUtil
import com.pos.device.printer.PrintCanvas


class GiroPrintNEW9220 : IGiroPrint {
    override fun printPaymentGiro(data: List<String>, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {

        val canvas = PrintCanvas()
        val paint = Paint()
        PrintManager.addLineBreakHeaderPaper(canvas, paint)

        PrintManager.setFontStyle(paint, 1, true)

        data.forEach {

            canvas.drawText(getLine(it), paint)

        }

        PrintManager.print(canvas, callback)


    }

    override fun printCaptureGiro(data: List<String>, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {

        val canvas = PrintCanvas()
        val paint = Paint()
        PrintManager.addLineBreakHeaderPaper(canvas, paint)

        PrintManager.setFontStyle(paint, 1, true)

        data.forEach {
            canvas.drawText(getLine(it), paint)

        }

        PrintManager.print(canvas, callback)
    }

    override fun printCheckRequest(
        girorCheckRequestsDTO: GirorCheckRequestsDTO,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        // implementar impresion de consulta de solicitudes

    }

    private fun validateCenter(line: String): Boolean {

        return line.split(RUtil.R_string(R.string.giro_print_format))[0] == RUtil.R_string(R.string.giro_print_aling_center)
    }

    private fun getLine(line: String): String {

        return if (!validateCenter(line))
            line.split(RUtil.R_string(R.string.giro_print_format))[1]
        else
            calculateCenterLine(line.split(RUtil.R_string(R.string.giro_print_format))[1])
    }

    private fun calculateCenterLine(line: String): String {

        var lineTem = line
        val maxiLineSize = 46
        val lineSize = line.length

        if (lineSize < maxiLineSize) {

            val lineCenter = (maxiLineSize - lineSize)

            lineTem = "${" ".repeat(lineCenter)}${lineTem}"

        }

        return lineTem
    }

    override fun printCloseBox(
        data: List<String>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        // implementar impresion de consulta de solicitudes
    }


}