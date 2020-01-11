package co.com.pagatodo.core.data.print

import android.graphics.Bitmap
import android.os.Build
import android.text.StaticLayout
import android.text.TextPaint
import androidx.annotation.RequiresApi
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.print.SportingPrintModel
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.formatValue
import java.lang.StringBuilder
import co.com.pagatodo.core.data.print.PrintManager.Companion.setFontStyle
import co.com.pagatodo.core.data.print.bluetooth.PrintBluetoothManager

class SportingPrint: BasePrint(), ISportingPrint {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun print(
        printModel: SportingPrintModel,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        val bitmap= PrintManager.createBitmap()
        val canvas = PrintManager.createCanvas(bitmap)
        val paint = TextPaint()

        val arrayHeader = printModel.header?.split("\\\\n")
        PrintManager.setFontStyle(paint, 1, false)
        canvas.drawText(arrayHeader?.get(0) ?: "NA",0F,100F, paint)

        val staticLayoutHeader = StaticLayout.Builder
            .obtain(arrayHeader?.get(1) ?: "NA", 0, arrayHeader?.get(1)?.length ?: 0, paint, canvas.width)
            .build()

        canvas.translate(0F, 100F)
        staticLayoutHeader.draw(canvas)

        var positionY = (30 * staticLayoutHeader.lineCount).toFloat()
        if (printModel.productCode == RUtil.R_string(R.string.code_sporting_league_product).toInt()){
            setFontStyle(paint, 1, false)
        }else{
            setFontStyle(paint, 2, false)
        }
        canvas.drawText(printModel.productName ?: "",0F,positionY, paint)
        positionY += 22
        canvas.drawText("${printModel.contractDate}",0F,positionY, paint)
        positionY += 22
        canvas.drawText("F. Venta: ${printModel.dateOfSale}",0F,positionY, paint)
        positionY += 22
        canvas.drawText("${RUtil.R_string(R.string.print_seller_equal_uppercamelcase)} ${printModel.sellerCode}   " +
                "M: ${getDeviceId()}",0F,positionY, paint)
        positionY += 22
        canvas.drawText("F. Grilla: ${printModel.grid}",0F,positionY, paint)
        positionY += 22
        canvas.drawText("Colilla: ${printModel.stub}${if(isRetry){
            RUtil.R_string(R.string.print_retry_icon)
        }else{""}}",0F,positionY, paint)
        positionY += 22
        canvas.drawText("Chequeo: ${printModel.digitChecked}",0F,positionY, paint)

        setFontStyle(paint, 2, true)
        positionY += 30
        var yPosition = positionY
        if (printModel.productCode == RUtil.R_string(R.string.code_sporting_league_product).toInt()){
            canvas.drawText("EC   Equipos    AP    EC    Equipos    AP",0F,yPosition, paint)
            yPosition += 5
            PrintManager.setFontStyle(paint, 1, false)
            var count = 1
            var indexItem = 0
            val teams = printModel.teams
            for (index in 0..6) {
                val rowString = StringBuilder()
                teams?.get(count-1)?.let {
                    rowString.append("${count.toString().padEnd(3)}.   ${it.local} Vs ${it.visitor}")
                }
                yPosition += 19
                canvas.drawText(rowString.toString(),0F,yPosition, paint)
                indexItem += 1
                count+=2
            }

            yPosition = positionY + 5
            indexItem = 0
            for (index in 0..6) {
                val rowString = StringBuilder()
                teams?.get(indexItem)?.let {
                    var ap = "E"
                    if (it.isVisitorResult ?: false){
                        ap = "V"
                    }else if (it.isLocalResult ?: false){
                        ap = "L"
                    }
                    rowString.append(ap)
                }
                indexItem += 2
                yPosition += 19
                canvas.drawText(rowString.toString(),150F,yPosition, paint)
            }

            yPosition = positionY + 5
            indexItem = 1
            count = 2
            for (index in 0..6) {
                val rowString = StringBuilder()
                teams?.get(count-1)?.let {
                    rowString.append("${count.toString().padEnd(3)}.   ${it.local} Vs ${it.visitor}")
                }
                indexItem += 2
                yPosition += 19
                canvas.drawText(rowString.toString(),200F,yPosition, paint)
                count+=2
            }

            yPosition = positionY + 5
            indexItem = 1
            count = 2
            for (index in 0..6) {
                val rowString = StringBuilder()
                teams?.get(count-1)?.let {
                    var ap = "E"
                    if (it.isVisitorResult ?: false){
                        ap = "V"
                    }else if (it.isLocalResult ?: false){
                        ap = "L"
                    }
                    rowString.append(ap)
                }
                indexItem += 2
                yPosition += 19
                canvas.drawText(rowString.toString(),345F,yPosition, paint)
                count+=2
            }

        }else{
            setFontStyle(paint, 2, true)
            canvas.drawText("EC    Equipos     Marcador    Equipos",0F,yPosition, paint)

            yPosition += 25
            setFontStyle(paint, 2, false)
            printModel.teams?.forEachIndexed { index, team ->
                canvas.drawText("${index+1}.",0F,yPosition,paint)

                canvas.drawText("${team.local}",60F,yPosition,paint)

                canvas.drawText("${team.localMarker} - ${team.visitorMarker}",170F,yPosition,paint)

                canvas.drawText("${team.visitor}",280F,yPosition,paint)

                yPosition += 25
            }
        }

        yPosition += 45

        setFontStyle(paint, 2, true)
        canvas.drawText("Total: $${formatValue((printModel.totalValue ?: "0").toDouble())}",0F,yPosition, paint)
        yPosition += 23
        canvas.drawText("IVA: $${formatValue(
            (printModel.iva ?: "0").toDouble()
        )} Neto: $${formatValue((printModel.value ?: "0").toDouble())}",0F,yPosition, paint)

        setFontStyle(paint, 2, false)
        yPosition += 23
        canvas.drawText("                   ${printModel.digitCheck}",0F,yPosition, paint)
        if (printModel.productCode == RUtil.R_string(R.string.code_sporting_mega_goals_product).toInt()){
            yPosition += 23
        }
        yPosition += 23
        val arrayFooter = printModel.footer?.split("\\\\n")
        arrayFooter?.forEachIndexed { index, item ->
            if (index == 0){
                setFontStyle(paint, 2, true)
            }else if (printModel.productCode == RUtil.R_string(R.string.code_sporting_league_product).toInt()){
                setFontStyle(paint, 1, false)
            }else{
                setFontStyle(paint, 2, false)
            }
            canvas.drawText(item,0F, yPosition, paint)
            yPosition += 20
        }
        val widthImage = canvas.width
        val heigthImage = (printModel.imageFooter?.height ?: 2)

        val imageScaled = Bitmap.createScaledBitmap(printModel.imageFooter,widthImage,(heigthImage/3), false)
        canvas.drawBitmap(imageScaled,0F, yPosition, paint)

        PrintBluetoothManager.printBitmap(bitmap, callback)
    }
}