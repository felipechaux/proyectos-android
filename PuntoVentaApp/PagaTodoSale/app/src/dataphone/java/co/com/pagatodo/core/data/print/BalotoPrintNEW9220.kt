package co.com.pagatodo.core.data.print

import android.graphics.*
import android.text.TextPaint
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.ExchangeTicketDTO
import co.com.pagatodo.core.data.model.print.BalotoPrintModel
import co.com.pagatodo.core.data.print.PrintManager.Companion.addLineBreak
import com.pos.device.printer.PrintCanvas
import com.pos.device.printer.Printer
import co.com.pagatodo.core.data.print.PrintManager.Companion.addLineBreakHeaderPaper
import co.com.pagatodo.core.data.print.PrintManager.Companion.setFontStyle
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.formatValue

class BalotoPrintNEW9220: BasePrint(), IBalotoPrint {

    override fun printBalotoSaleTicket(printModel:BalotoPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        when(PrinterStatusInfo().getStatus()) {
            PrinterStatus.OK -> {

                val canvas = PrintCanvas()
                val paint = Paint()

                addLineBreakHeaderPaper(canvas, paint)
                addLineBreak(canvas, paint)
                setFontStyle(paint, 3, true)
                canvas.drawText("        ${R_string(R.string.print_baloto_title)}", paint)

                setFontStyle(paint, 1, false)
                splitDataHeaderTicket(printModel.headerBaloto ?: "").forEach {
                    canvas.drawText(it, paint)
                }

                setFontStyle(paint, 2, false)
                canvas.drawText(printModel.transactionDate, paint)

                canvas.drawText("                                      ${R_string(R.string.print_super_baloto)}", paint)
                printModel.numbers?.forEach { item ->
                    canvas.drawText(item, paint)
                }

                val posYDinamic = 390
                canvas.setY(posYDinamic)
                canvas.drawText("${R_string(R.string.print_baloto_value)} ${printModel.valueBaloto} ${R_string(R.string.print_baloto_revenge_value)} ${printModel.valueRevenge}", paint)
                canvas.setY((posYDinamic+20))
                canvas.drawText("${R_string(R.string.text_label_iva_uppercase)} ${printModel.iva}", paint)
                canvas.setY((posYDinamic+40))
                canvas.drawText("${R_string(R.string.print_baloto_without_iva_total)} ${printModel.withoutIvaTotal}", paint)
                canvas.setY((posYDinamic+60))
                canvas.drawText("${R_string(R.string.text_label_total_value).toUpperCase()} ${printModel.total}", paint)

                val sideCurrtentBoxPosY = posYDinamic + 90
                canvas.setY(sideCurrtentBoxPosY)
                canvas.drawText("                   ${R_string(R.string.print_baloto_keep_playing)}", paint)
                canvas.setY((sideCurrtentBoxPosY+20))
                canvas.drawText(printModel.drawInfo, paint)

                canvas.setY(sideCurrtentBoxPosY+90)
                addLineBreak(canvas, paint, 3)
                PrintManager.print(canvas){
                    printBalotoTransactionTicket(printModel, callback)
                }
            }
        }
    }

    fun printBalotoTransactionTicket(printModel:BalotoPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?) {
        val canvas = PrintCanvas()
        val paint = Paint()

        addLineBreakHeaderPaper(canvas, paint)
        addLineBreak(canvas, paint)
        setFontStyle(paint, 2, false)

        canvas.drawText("${R_string(R.string.print_baloto_num)} ${printModel.serialNumber}", paint)
        canvas.drawText("${R_string(R.string.print_baloto_seller)} ${printModel.sellerCode} ${R_string(R.string.print_baloto_term)} ${getDeviceId()}", paint)
        canvas.drawText("${R_string(R.string.print_seller_equal_upper)} ${printModel.sellerCode}", paint)
        canvas.drawText("${R_string(R.string.print_stub_equal)} ${printModel.stubs}", paint)

        canvas.drawText("${R_string(R.string.print_check_equal)} ${printModel.checkNumber}", paint)
        canvas.drawText("        ${printModel.transactionNumber}", paint)
        canvas.drawText(printModel.footerBaloto?.replace("\\n", ""), paint)

        addLineBreak(canvas, paint, 0)
        PrintManager.print(canvas, callback)
    }

    override fun printBalotoQueryTicket(
        transactionDate: String,
        numberTicket: String,
        valuePrize: String,
        tax: String,
        valuePay: String,
        terminalCode: String,
        salePointCode: String,
        authorizerNumber: String,
        stub: String,
        sellerCode: String,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        when(PrintManager.checkPrinterStatus()) {
            Printer.PRINTER_OK -> {
                val bitmapCanvas = Bitmap.createBitmap(380,640, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmapCanvas)
                val paint = TextPaint()

                paint.textAlign = Paint.Align.CENTER
                val bounds = RectF(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat())

                setFontStyle(paint, 3, false)
                canvas.drawText("${R_string(R.string.text_print_baloto_title)}", bounds.centerX(),90F, paint)
                canvas.drawText("",0F,120F,paint)
                setFontStyle(paint, 2, false)
                canvas.drawText("${transactionDate}",bounds.centerX(),150F, paint)
                canvas.drawText("",0F,180F,paint)
                canvas.drawText("${R_string(R.string.text_print_baloto_subtitle)}",bounds.centerX(),210F, paint)
                canvas.drawText("${R_string(R.string.text_print_baloto_number)} ${numberTicket}", bounds.centerX(),240F, paint)
                canvas.drawText("${R_string(R.string.text_print_baloto_prize)} $${formatValue(valuePrize.toDouble())}", bounds.centerX(),270F, paint)
                canvas.drawText("${R_string(R.string.text_print_imp_ret)} $${formatValue(tax.toDouble())}", bounds.centerX(),300F, paint)
                canvas.drawText("${R_string(R.string.text_print_baloto_pay)} $${formatValue(valuePay.toDouble())}", bounds.centerX(),330F, paint)
                canvas.drawText("${R_string(R.string.text_print_baloto_terminal)} ${getDeviceId()}", bounds.centerX(),360F, paint)
                canvas.drawText("${R_string(R.string.text_print_baloto_pto_vta)} ${getDeviceId()}", bounds.centerX(),390F, paint)
                canvas.drawText("",0F,420F,paint)
                canvas.drawText(R_string(R.string.text_print_baloto_number_authorizer), bounds.centerX(),450F, paint)
                canvas.drawText(authorizerNumber,bounds.centerX(),480F, paint)
                canvas.drawText("",0F,510F,paint)
                canvas.drawText("${R_string(R.string.text_print_baloto_stub)} ${stub}", bounds.centerX(),540F, paint)
                canvas.drawText("${R_string(R.string.text_print_baloto_seller_code)} ${sellerCode}", bounds.centerX(),570F, paint)

                val printCanvas = PrintCanvas()
                val paintCanvas = Paint()
                printCanvas.drawBitmap(bitmapCanvas, paintCanvas)

                PrintManager.print(printCanvas, callback)
            }
        }
    }

    private fun splitDataHeaderTicket(text: String): List<String> {
        val list = mutableListOf<String>()
        text.split("\\n").forEach {
            list.add(it.trim())
        }
        return list
    }
}