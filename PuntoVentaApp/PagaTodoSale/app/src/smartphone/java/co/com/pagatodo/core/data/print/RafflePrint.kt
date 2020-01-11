package co.com.pagatodo.core.data.print

import android.os.Build
import android.text.StaticLayout
import android.text.TextPaint
import androidx.annotation.RequiresApi
import co.com.pagatodo.core.data.model.print.RafflePrintModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.data.print.PrintManager.Companion.setFontStyle
import co.com.pagatodo.core.data.print.bluetooth.PrintBluetoothManager

class RafflePrint: BasePrint(), IRafflePrint {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun print(
        printModel: RafflePrintModel,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        val bitmap= PrintManager.createBitmap()
        val canvas = PrintManager.createCanvas(bitmap)
        val paint = TextPaint()

        setFontStyle(paint, 2, false)
        canvas.drawText("${getCurrentDate(DateUtil.StringFormat.DMMMYYHOUR_SPLIT_WHITE_SPACE)}",60f,100F, paint)
        canvas.drawText("Asesora:${printModel.sellerCode}  M:${getDeviceId()}",0F,125F,paint)
        canvas.drawText("Colilla:   ${printModel.stub}",0F,150F,paint)
        canvas.drawText("SER. UNICO:${printModel.uniqueSerial}",0F,175F, paint)
        canvas.drawText("Chequeo: ${printModel.digitChecked}",0F,200F,paint)

        val name = "Rifa:${printModel.raffleName}"
        val staticLayoutName = StaticLayout.Builder
            .obtain(name, 0, name.length, paint, canvas.width)
            .build()

        canvas.translate(0F, 200F)
        staticLayoutName.draw(canvas)

        var positionY = (50 * staticLayoutName.lineCount).toFloat()

        canvas.drawText("Número: ${printModel.raffleNumber}   Lot: ${printModel.lotery}",0F,positionY,paint)
        positionY += 40
        canvas.drawText("Valor rifa:$${printModel.raffleValue} IVA INCLUIDO",0F,positionY, paint)
        positionY += 25
        canvas.drawText("Valor Premio:$${printModel.rafflePrize}",0F,positionY,paint)
        positionY += 25
        canvas.drawText("Datos Sorteo:${printModel.dateDraw}",0F,positionY,paint)
        val staticLayoutDetail = StaticLayout.Builder
            .obtain(printModel.raffleDescription ?: "NA", 0, printModel.raffleDescription?.length ?: 0, paint, canvas.width)
            .build()

        positionY += 5
        canvas.translate(0F, positionY)
        staticLayoutDetail.draw(canvas)

        PrintBluetoothManager.printBitmap(bitmap, callback)
    }
}