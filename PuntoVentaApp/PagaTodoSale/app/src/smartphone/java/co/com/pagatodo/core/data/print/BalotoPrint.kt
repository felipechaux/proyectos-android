package co.com.pagatodo.core.data.print

import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.text.StaticLayout
import android.text.TextPaint
import androidx.annotation.RequiresApi
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.print.BalotoPrintModel
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.data.print.PrintManager.Companion.setFontStyle
import co.com.pagatodo.core.data.print.bluetooth.PrintBluetoothManager
import co.com.pagatodo.core.util.formatValue

class BalotoPrint: BasePrint(), IBalotoPrint {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun printBalotoSaleTicket(
        balotoPrintModel: BalotoPrintModel,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        val bitmap= PrintManager.createBitmap()
        val canvas = PrintManager.createCanvas(bitmap)
        val paint = TextPaint()

        setFontStyle(paint, 3, true)
        canvas.drawText("        ${RUtil.R_string(R.string.print_baloto_title)}",0F,100F, paint)

        var positionYHeader = 120F
        setFontStyle(paint, 1, false)
        splitDataHeaderTicket(balotoPrintModel.headerBaloto ?: "").forEachIndexed { index, item ->
            val text = if (index == 1) item.toUpperCase() else item
            canvas.drawText(text,0F,positionYHeader, paint)
            positionYHeader += 20
        }

        setFontStyle(paint, 2, false)
        canvas.drawText(balotoPrintModel.transactionDate ?: "",0F, positionYHeader, paint)

        positionYHeader += 25
        canvas.drawText("${RUtil.R_string(R.string.print_super_baloto)}",200F,positionYHeader, paint)

        positionYHeader += 25
        balotoPrintModel.numbers?.forEach { item ->
            val board = item.split("-")

            canvas.drawText(board[0],0F,positionYHeader, paint)
            canvas.drawText(board[1],240F,positionYHeader, paint)
            positionYHeader += 25
        }

        var posYDinamic = 425F
        canvas.drawText("${RUtil.R_string(R.string.print_baloto_value)} ${balotoPrintModel.valueBaloto} ${RUtil.R_string(R.string.print_baloto_revenge_value)} ${balotoPrintModel.valueRevenge}",0F, posYDinamic, paint)
        posYDinamic += 25
        canvas.drawText("${RUtil.R_string(R.string.text_label_iva_uppercase)} ${balotoPrintModel.iva}",0F,posYDinamic, paint)
        posYDinamic += 25
        canvas.drawText("${RUtil.R_string(R.string.print_baloto_without_iva_total)} ${balotoPrintModel.withoutIvaTotal}",0F,posYDinamic, paint)
        posYDinamic += 25
        canvas.drawText("${RUtil.R_string(R.string.text_label_total_value).toUpperCase()} ${balotoPrintModel.total}",0F,posYDinamic, paint)

        val sideCurrtentBoxPosY = posYDinamic + 30
        canvas.drawText("                   ${RUtil.R_string(R.string.print_baloto_keep_playing)}",0F,sideCurrtentBoxPosY, paint)

        val staticLayoutDates = StaticLayout.Builder
            .obtain(balotoPrintModel.drawInfo, 0, balotoPrintModel.drawInfo?.length ?: 0, paint, canvas.width)
            .build()

        canvas.translate(0F, sideCurrtentBoxPosY)
        staticLayoutDates.draw(canvas)

        PrintBluetoothManager.printBitmap(bitmap){
            printBalotoTransactionTicket(balotoPrintModel, callback)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun printBalotoTransactionTicket(printModel:BalotoPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?) {
        val bitmap= PrintManager.createBitmap()
        val canvas = PrintManager.createCanvas(bitmap)
        val paint = TextPaint()

        setFontStyle(paint, 2, false)

        canvas.drawText("${RUtil.R_string(R.string.print_baloto_num)} ${printModel.serialNumber}",0f,100F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_baloto_seller)} ${printModel.sellerCode} ${RUtil.R_string(R.string.print_baloto_term)} ${getDeviceId()}",0F, 120F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_seller_equal_upper)} ${printModel.sellerCode}",0F,140F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_stub_equal)} ${printModel.stubs}",0F,160F, paint)

        canvas.drawText("${RUtil.R_string(R.string.print_check_equal)} ${printModel.checkNumber}",0F,180F, paint)
        canvas.drawText("        ${printModel.transactionNumber}",0F,200F, paint)

        val textFooter = printModel.footerBaloto?.replace("\\n", "")
        val staticLayoutDates = StaticLayout.Builder
            .obtain(textFooter, 0, textFooter?.length ?: 0, paint, canvas.width)
            .build()

        canvas.translate(0F, 200F)
        staticLayoutDates.draw(canvas)

        PrintBluetoothManager.printBitmap(bitmap, callback)
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
        val bitmap= PrintManager.createBitmap()
        val canvas = PrintManager.createCanvas(bitmap)
        val paint = TextPaint()

        paint.textAlign = Paint.Align.CENTER
        val bounds = RectF(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat())

        setFontStyle(paint, 3, false)
        canvas.drawText("${RUtil.R_string(R.string.text_print_baloto_title)}", bounds.centerX(),90F, paint)

        setFontStyle(paint, 2, false)
        canvas.drawText("${transactionDate}",bounds.centerX(),150F, paint)

        canvas.drawText("${RUtil.R_string(R.string.text_print_baloto_subtitle)}",bounds.centerX(),210F, paint)
        canvas.drawText("${RUtil.R_string(R.string.text_print_baloto_number)} ${numberTicket}", bounds.centerX(),240F, paint)
        canvas.drawText("${RUtil.R_string(R.string.text_print_baloto_prize)} $${formatValue(valuePrize.toDouble())}", bounds.centerX(),270F, paint)
        canvas.drawText("${RUtil.R_string(R.string.text_print_imp_ret)} $${formatValue(tax.toDouble())}", bounds.centerX(),300F, paint)
        canvas.drawText("${RUtil.R_string(R.string.text_print_baloto_pay)} $${formatValue(valuePay.toDouble())}", bounds.centerX(),330F, paint)
        canvas.drawText("${RUtil.R_string(R.string.text_print_baloto_terminal)} ${getDeviceId()}", bounds.centerX(),360F, paint)
        canvas.drawText("${RUtil.R_string(R.string.text_print_baloto_pto_vta)} ${getDeviceId()}", bounds.centerX(),390F, paint)

        canvas.drawText(RUtil.R_string(R.string.text_print_baloto_number_authorizer), bounds.centerX(),450F, paint)
        canvas.drawText(authorizerNumber,bounds.centerX(),480F, paint)

        canvas.drawText("${RUtil.R_string(R.string.text_print_baloto_stub)} ${stub}", bounds.centerX(),540F, paint)
        canvas.drawText("${RUtil.R_string(R.string.text_print_baloto_seller_code)} ${sellerCode}", bounds.centerX(),570F, paint)

        PrintBluetoothManager.printBitmap(bitmap, callback)
    }

    private fun splitDataHeaderTicket(text: String): List<String> {
        val list = mutableListOf<String>()
        text.split("\\n").forEach {
            list.add(it.trim())
        }
        return list
    }
}