package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.PagaTodoApplication.Companion.format
import co.com.pagatodo.core.PagaTodoApplication.Companion.printerDevice
import co.com.pagatodo.core.data.model.print.RafflePrintModel
import co.com.pagatodo.core.util.DateUtil
import com.cloudpos.printer.Format.*

class RafflePrintQ2 : BasePrint(), IRafflePrint {

    override fun print(
        printModel: RafflePrintModel,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        val builder = StringBuilder()
        try {

            builder.append("<div style =\"overflow: hidden;height:545px\">")
            builder.append("<br>")
            builder.append("<br>")
            builder.append("<br>")
            builder.append("<center>")
            builder.append("<font size=\"4\"><strong> ${getCurrentDate(DateUtil.StringFormat.DMMMYYHOUR_SPLIT_WHITE_SPACE)}</strong></font><br>")
            builder.append("</center>")
            builder.append("<p>")
            builder.append("<font size=\"4\"><strong> Asesora:${printModel.sellerCode}  M:${getDeviceId()} </strong></font><br>")
            builder.append("<font size=\"4\"><strong>Colilla:   ${printModel.stub}</strong></font><br>")
            builder.append("<font size=\"4\"><strong>SER. UNICO:${printModel.uniqueSerial}</strong></font><br>")
            builder.append("<font size=\"4\"><strong>Chequeo: ${printModel.digitChecked}</strong></font><br>")
            builder.append("<font size=\"4\"><strong>Rifa:${printModel.raffleName}</strong></font><br>")
            builder.append("<font size=\"4\"><strong>Numero: ${printModel.raffleNumber}   Lot: ${printModel.lotery}</strong></font><br>")
            builder.append("<br>")
            builder.append("<font size=\"4\"><strong>Valor rifa:$${printModel.raffleValue} IVA INCLUIDO</strong></font><br>")
            builder.append("<font size=\"4\"><strong>Valor Premio:$${printModel.rafflePrize}</strong></font><br>")
            builder.append("<font size=\"4\"><strong>Datos Sorteo:${printModel.dateDraw} ${printModel.timeDraw}</strong></font><br>")
            builder.append("<br>")
            builder.append("<font size=\"4\"><strong>${printModel.raffleDescription}</strong></font><br>")
            builder.append("</p>")
            builder.append("</div>")
            builder.append("<br>")
        } catch (e: Exception) {
            e.printStackTrace()
            callback(PrinterStatus.ERROR)

        }
        executeHtmlPrintQ2(builder.toString(), 0, 0,0, callback)


    }

}