package co.com.pagatodo.core.data.print

import android.graphics.Paint
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.print.VirtualWalletPrintModel
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import com.pos.device.printer.PrintCanvas

class VirtualWalletPrintNEW9220 : BasePrint(), IVirtualWalletPrint {

    val stringBuilder = StringBuilder()

    override fun print(printModel: VirtualWalletPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?) {

        val canvas = PrintCanvas()
        val paint = Paint()
        PrintManager.addLineBreakHeaderPaper(canvas, paint)

        PrintManager.setFontStyle(paint, 1, true)

        val header = printModel.textHeader?.split("\\n")
        val body = printModel.textBody?.split("\\n")

        header?.forEachIndexed { _, item ->
            canvas.drawText(calculateCenterLine(item), paint)
        }

        canvas.drawText("  ", paint)

        body?.forEachIndexed { _, item ->
            canvas.drawText(calculateCenterLine(item), paint)

        }

        canvas.drawText("  ", paint)
        canvas.drawText("  ", paint)
        canvas.drawText("  ", paint)

        canvas.drawText("Pin: ${printModel.pin}", paint)
        canvas.drawText("Usuario: ${getPreference<String>(R_string(R.string.shared_key_seller_code))}", paint)
        canvas.drawText("Valor Pin: ${printModel.pinValue}", paint)

        canvas.drawText("  ", paint)
        canvas.drawText("  ", paint)
        canvas.drawText("  ", paint)
        canvas.drawText("  ", paint)
        canvas.drawText("  ", paint)

        try {
            PrintManager.print(canvas, callback)
        } catch (e: Exception) {
            callback(PrinterStatus.ERROR)
        }


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


}