package co.com.pagatodo.core.data.print

import android.graphics.Bitmap
import android.graphics.Paint
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.print.SportingPrintModel
import co.com.pagatodo.core.data.print.PrintManager.Companion.setFontStyle
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.formatValue
import com.pos.device.printer.PrintCanvas
import java.lang.StringBuilder

class SportingPrintNEW9220: BasePrint(), ISportingPrint {

    override fun print(printModel: SportingPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        val canvas = PrintCanvas()
        val paint = Paint()
        PrintManager.addLineBreakHeaderPaper(canvas, paint)

        val arrayHeader = printModel.header?.split("\\\\n")
        setFontStyle(paint, 1, false)
        canvas.drawText(arrayHeader?.get(0) ?: "NA", paint)
        canvas.drawText(arrayHeader?.get(1) ?: "NA", paint)
        if (printModel.productCode == R_string(R.string.code_sporting_league_product).toInt()){
            setFontStyle(paint, 1, false)
        }else{
            setFontStyle(paint, 2, false)
        }
        canvas.drawText(printModel.productName, paint)
        canvas.drawText("${printModel.contractDate}", paint)
        canvas.drawText("F. Venta: ${printModel.dateOfSale}", paint)
        canvas.drawText("${R_string(R.string.print_seller_equal_uppercamelcase)} ${printModel.sellerCode}   M: ${getDeviceId()}", paint)
        canvas.drawText("F. Grilla: ${printModel.grid}", paint)
        canvas.drawText("Colilla: ${printModel.stub}${if(isRetry){
            R_string(R.string.print_retry_icon)}else{""}}", paint)
        canvas.drawText("Chequeo: ${printModel.digitChecked}", paint)

        setFontStyle(paint, 2, true)
        var yPosition = 260
        if (printModel.productCode == R_string(R.string.code_sporting_league_product).toInt()){
            canvas.drawText("EC   Equipos    AP    EC    Equipos    AP", paint)
            setFontStyle(paint, 1, false)
            var count = 1
            var indexItem = 0
            val teams = printModel.teams
            canvas.setY(yPosition)
            for (index in 0..6) {
                val rowString = StringBuilder()
                teams?.get(count-1)?.let {
                    rowString.append("${count.toString().padEnd(3)}.   ${it.local} Vs ${it.visitor}")
                }
                yPosition += 18
                canvas.setY(yPosition)
                canvas.drawText(rowString.toString(), paint)
                indexItem += 1
                count+=2
            }

            yPosition = 260
            indexItem = 0
            canvas.setY(yPosition)
            for (index in 0..6) {
                val rowString = StringBuilder()
                teams?.get(indexItem)?.let {
                    var ap = "E"
                    if (it.isVisitorResult ?: false){
                        ap = "V"
                    }else if (it.isLocalResult ?: false){
                        ap = "L"
                    }
                    rowString.append("${ap}")
                }
                indexItem += 2
                yPosition += 18
                canvas.setX(150)
                canvas.setY(yPosition)
                canvas.drawText(rowString.toString(), paint)
            }

            yPosition = 260
            indexItem = 1
            count = 2
            canvas.setY(yPosition)
            for (index in 0..6) {
                val rowString = StringBuilder()
                teams?.get(count-1)?.let {
                    rowString.append("${count.toString().padEnd(3)}.   ${it.local} Vs ${it.visitor}")
                }
                indexItem += 2
                yPosition += 18
                canvas.setY(yPosition)
                canvas.setX(200)
                canvas.drawText(rowString.toString(), paint)
                count+=2
            }

            yPosition = 260
            indexItem = 1
            count = 2
            canvas.setY(yPosition)
            for (index in 0..6) {
                val rowString = StringBuilder()
                teams?.get(count-1)?.let {
                    var ap = "E"
                    if (it.isVisitorResult ?: false){
                        ap = "V"
                    }else if (it.isLocalResult ?: false){
                        ap = "L"
                    }
                    rowString.append("${ap}")
                }
                indexItem += 2
                yPosition += 18
                canvas.setY(yPosition)
                canvas.setX(345)
                canvas.drawText(rowString.toString(), paint)
                count+=2
            }

        }else{
            setFontStyle(paint, 2, true)
            canvas.drawText("EC    Equipos     Marcador    Equipos", paint)
            setFontStyle(paint, 2, false)

            yPosition += 55
            printModel.teams?.forEachIndexed { index, team ->
                canvas.setY(yPosition)
                canvas.setX(0)
                canvas.drawText("${index + 1}.", paint)

                canvas.setY(yPosition)
                canvas.setX(50)
                canvas.drawText("${team.local}", paint)

                canvas.setY(yPosition)
                canvas.setX(165)
                canvas.drawText("${team.localMarker} - ${team.visitorMarker}", paint)

                canvas.setY(yPosition)
                canvas.setX(270)
                canvas.drawText("${team.visitor}", paint)

                yPosition += 20
            }
        }

        if (printModel.productCode == R_string(R.string.code_sporting_mega_goals_product).toInt()){
            PrintManager.addLineBreak(canvas, paint)
        }

        setFontStyle(paint, 2, true)
        canvas.drawText("Total: $${formatValue((printModel.totalValue ?: "0").toDouble())}", paint)
        canvas.drawText("IVA: $${formatValue(
            (printModel.iva ?: "0").toDouble()
        )} Neto: $${formatValue((printModel.value ?: "0").toDouble())}", paint)
        setFontStyle(paint, 2, false)
        canvas.drawText("                   ${printModel.digitCheck}", paint)
        if (printModel.productCode == R_string(R.string.code_sporting_mega_goals_product).toInt()){
            PrintManager.addLineBreak(canvas, paint)
        }
        val arrayFooter = printModel.footer?.split("\\\\n")
        arrayFooter?.forEachIndexed { index, item ->
            if (index == 0){
                setFontStyle(paint, 2, true)
            }else if (printModel.productCode == R_string(R.string.code_sporting_league_product).toInt()){
                setFontStyle(paint, 1, false)
            }else{
                setFontStyle(paint, 2, false)
            }
            canvas.drawText(item, paint)
        }
        PrintManager.addLineBreak(canvas, paint)
        val widthImage = (printModel.imageFooter?.width ?: 2) / 2
        val heigthImage = (printModel.imageFooter?.height ?: 2) / 2

        val imageScaled = Bitmap.createScaledBitmap(printModel.imageFooter,widthImage,heigthImage, false)
        canvas.drawBitmap(imageScaled, paint)
        if (printModel.productCode == R_string(R.string.code_sporting_league_product).toInt()){
            PrintManager.addLineBreak(canvas, paint)
        }
        PrintManager.print(canvas, callback)
    }

}