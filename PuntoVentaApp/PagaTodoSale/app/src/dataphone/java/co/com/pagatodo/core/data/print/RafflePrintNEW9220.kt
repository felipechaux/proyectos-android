package co.com.pagatodo.core.data.print

import android.graphics.Paint
import co.com.pagatodo.core.data.model.print.RafflePrintModel
import co.com.pagatodo.core.data.print.PrintManager.Companion.addLineBreak
import co.com.pagatodo.core.data.print.PrintManager.Companion.addLineBreakHeaderPaper
import co.com.pagatodo.core.data.print.PrintManager.Companion.setFontStyle
import co.com.pagatodo.core.util.DateUtil
import com.pos.device.printer.PrintCanvas

class RafflePrintNEW9220: BasePrint(), IRafflePrint {

    override fun print(printModel: RafflePrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        val canvas = PrintCanvas()
        val paint = Paint()
        addLineBreakHeaderPaper(canvas, paint)
        setFontStyle(paint, 2, true)
        canvas.drawText("            ${getCurrentDate(DateUtil.StringFormat.DMMMYYHOUR_SPLIT_WHITE_SPACE)}", paint)
        canvas.drawText("Asesora:${printModel.sellerCode}  M:${getDeviceId()}",paint)
        canvas.drawText("Colilla:   ${printModel.stub}",paint)
        canvas.drawText("SER. UNICO:${printModel.uniqueSerial}", paint)
        canvas.drawText("Chequeo: ${printModel.digitChecked}",paint)
        canvas.drawText("Rifa:${printModel.raffleName}", paint)
        canvas.drawText("Número: ${printModel.raffleNumber}   Lot: ${printModel.lotery}",paint)
        addLineBreak(canvas, paint)
        canvas.drawText("Valor rifa:$${printModel.raffleValue} IVA INCLUIDO", paint)
        canvas.drawText("Valor Premio:$${printModel.rafflePrize}",paint)
        canvas.drawText("Datos Sorteo:${printModel.dateDraw}",paint)
        canvas.drawText(printModel.raffleDescription, paint)

        val positionY = 545
        canvas.setY(positionY)
        addLineBreak(canvas, paint, 3)
        PrintManager.print(canvas, callback)
    }
}