package co.com.pagatodo.core.data.print

import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.annotation.RequiresApi
import co.com.pagatodo.core.PagaTodoApplication.Companion.format
import co.com.pagatodo.core.PagaTodoApplication.Companion.printerDevice
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.print.SportingPrintModel
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.formatValue
import com.cloudpos.printer.Format

class SportingPrintQ2 : BasePrint(), ISportingPrint {

    @RequiresApi(Build.VERSION_CODES.P)
    override fun print(
        printModel: SportingPrintModel,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        if (openPrinter() != 0) {
            callback(PrinterStatus.BUSY)
            return
        }
        try {

            addLineBreak(3)

            val arrayHeader = printModel.header?.split("\\\\n")
            setFontStyle(format, 2, false, Format.FORMAT_ALIGN_LEFT)
            printerDevice.printText(format, arrayHeader?.get(0) ?: "NA")
            printerDevice.printText(format, arrayHeader?.get(1) ?: "NA")
            if (printModel.productCode == RUtil.R_string(R.string.code_sporting_league_product).toInt()) {
                setFontStyle(format, 2, false, Format.FORMAT_ALIGN_LEFT)
            } else {
                setFontStyle(format, 3, false, Format.FORMAT_ALIGN_LEFT)
            }
            printerDevice.printText(
                format, printModel.productName
            )
            printerDevice.printText(format, "${printModel.contractDate}")
            printerDevice.printText(format, "F. Venta: ${printModel.dateOfSale}")
            printerDevice.printText(
                format,
                "${RUtil.R_string(R.string.print_seller_equal_uppercamelcase)} ${printModel.sellerCode}   M: ${getDeviceId()}"
            )
            printerDevice.printText(format, "F. Grilla: ${printModel.grid}")
            printerDevice.printText(
                format,
                "Colilla: ${printModel.stub}${if (isRetry) {
                    RUtil.R_string(R.string.print_retry_icon)
                } else {
                    ""
                }}"
            )
            printerDevice.printText(format, "Chequeo: ${printModel.digitChecked}")

            setFontStyle(format, 2, true, Format.FORMAT_ALIGN_LEFT)
            if (printModel.productCode == RUtil.R_string(R.string.code_sporting_league_product).toInt()) {
                printerDevice.printText(format, "EC   Equipos    AP    EC    Equipos    AP")

                setFontStyle(format, 2, false, Format.FORMAT_ALIGN_LEFT)
                var oddColumn = 1
                var pairColumn = 2
                val teams = printModel.teams

                for (index in 0..6) {
                    val rowString = StringBuilder()
                    teams?.get(oddColumn - 1)?.let {
                        rowString.append("${oddColumn.toString().padEnd(2)}. ${it.local} Vs ${it.visitor}  ")
                        var ap = "E"
                        if (it.isVisitorResult ?: false) {
                            ap = "V"
                        } else if (it.isLocalResult ?: false) {
                            ap = "L"
                        }
                        rowString.append("${ap}     ")
                    }
                    oddColumn += 2

                    teams?.get(pairColumn - 1)?.let {
                        rowString.append("${pairColumn.toString().padEnd(2)}.  ${it.local} Vs ${it.visitor}  ")
                        var ap = "E"
                        if (it.isVisitorResult ?: false) {
                            ap = "V"
                        } else if (it.isLocalResult ?: false) {
                            ap = "L"
                        }
                        rowString.append("${ap}")
                    }
                    printerDevice.printText(format, rowString.toString())
                    pairColumn += 2
                }
            } else {
                val rowString = StringBuilder()
                setFontStyle(format, 2, true, Format.FORMAT_ALIGN_LEFT)
                printerDevice.printText(format, "EC    Equipos   Marcador     Equipos")
                setFontStyle(format, 3, false, Format.FORMAT_ALIGN_LEFT)

                printModel.teams?.forEachIndexed { index, team ->
                    rowString.append("${team.code!!.toInt()}.   ")
                    rowString.append("${team.local}    ")
                    rowString.append("${team.localMarker} - ${team.visitorMarker}     ")
                    rowString.append("${team.visitor}")
                    printerDevice.printText(format, rowString.toString())
                    rowString.clear()
                }
            }

            if (printModel.productCode == RUtil.R_string(R.string.code_sporting_mega_goals_product).toInt()) {
                addLineBreak(2)
            } else addLineBreak(1)

            printerDevice.printBitmap(
                drawText(
                    "Total: \$${formatValue((printModel.totalValue ?: "0").toDouble())}",
                    400,
                    Layout.Alignment.ALIGN_LEFT,
                    21f,
                    true,
                    Typeface.SANS_SERIF
                )
            )

            printerDevice.printBitmap(
                drawText(
                    "IVA: \$${formatValue(
                        (printModel.iva ?: "0").toDouble()
                    )} Neto: \$${formatValue((printModel.value ?: "0").toDouble())}",
                    400, Layout.Alignment.ALIGN_LEFT, 21f, true, Typeface.SANS_SERIF
                )
            )

            printerDevice.printBitmap(
                drawText(
                    "${printModel.digitCheck}",
                    400,
                    Layout.Alignment.ALIGN_CENTER,
                    21f,
                    true,
                    Typeface.SANS_SERIF
                )
            )

            if (printModel.productCode == RUtil.R_string(R.string.code_sporting_mega_goals_product).toInt()) {
                addLineBreak(1)
            }

            val arrayFooter = printModel.footer?.split("\\\\n")
            arrayFooter?.forEachIndexed { index, item ->
                printerDevice.printBitmap(
                    drawText(
                        "${item}",
                        400,
                        Layout.Alignment.ALIGN_LEFT,
                        21f,
                        true,
                        Typeface.SANS_SERIF
                    )
                )
            }
            addLineBreak(1)

            val widthImage = (printModel.imageFooter?.width ?: 2) / 2
            val heigthImage = (printModel.imageFooter?.height ?: 2) / 2

            val imageScaled = Bitmap.createScaledBitmap(printModel.imageFooter, widthImage, heigthImage, false)
            printerDevice.printBitmap(imageScaled)
            addLineBreak(4)

            callback(PrinterStatus.OK)
            finalizePrinter()

        } catch (e: Exception) {
            e.printStackTrace()
            callback(PrinterStatus.ERROR)
            finalizePrinter()
        }
    }


    private fun drawText(
        text: String,
        textWidth: Int,
        align: Layout.Alignment,
        textSize: Float,
        bold: Boolean,
        typeface: Typeface
    ): Bitmap {

        // Get text dimensions
        val textPaint = TextPaint(ANTI_ALIAS_FLAG or Paint.LINEAR_TEXT_FLAG)

        textPaint.setColor(Color.BLACK)
        textPaint.setTextSize(textSize)
        textPaint.setTypeface(typeface)
        textPaint.setFakeBoldText(bold)
        val mTextLayout = StaticLayout(
            text, textPaint,
            textWidth, align, 0.0f, 0.0f, false
        )

        // Create bitmap and canvas to draw to
        val b = Bitmap.createBitmap(textWidth, mTextLayout.height, Bitmap.Config.RGB_565)
        val canvas = Canvas(b)

        // Draw background
        val paint = Paint(ANTI_ALIAS_FLAG or Paint.LINEAR_TEXT_FLAG)
        paint.setStyle(Paint.Style.FILL)
        paint.setColor(Color.WHITE)
        paint.setTextSize(18f)
        canvas.drawPaint(paint)

        // Draw text
        canvas.save()
        canvas.translate(0f, 0f)

        mTextLayout.draw(canvas)
        canvas.restore()

        return b
    }

}