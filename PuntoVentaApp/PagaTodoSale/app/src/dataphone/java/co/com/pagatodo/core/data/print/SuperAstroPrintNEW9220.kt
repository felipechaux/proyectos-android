package co.com.pagatodo.core.data.print

import android.graphics.Paint
import android.graphics.Typeface
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.print.SuperAstroPrintModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import com.pos.device.printer.PrintCanvas
import java.lang.StringBuilder

class SuperAstroPrintNEW9220: BasePrint(), ISuperAstroPrint {

    override fun print(printModel: SuperAstroPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        val canvas = PrintCanvas()
        val paint = Paint()
        PrintManager.addLineBreakHeaderPaper(canvas, paint)

        paint.textSize = 19f

        val arrayHeader = printModel.textHeader?.split("\\\\n")
        arrayHeader?.forEach {
            canvas.drawText(it, paint)
        }

        paint.textSize = 21f
        canvas.drawText("${R_string(R.string.print_date_equal)} ${getCurrentDate(DateUtil.StringFormat.DDMMYYHOUR_SPLIT_WHITE_SPACE)}", paint)
        canvas.drawText("${R_string(R.string.print_draw_date_equal)} ${printModel.drawDate}", paint)

        paint.textSize = 22f
        canvas.drawText("${R_string(R.string.print_serie)} :     ${printModel.stub}${if(isRetry){
            R_string(R.string.print_retry_icon)}else{""}}", paint)
        paint.textSize = 21f
        canvas.drawText("${R_string(R.string.print_draw_text)}(${printModel.lotteries?.count() ?: 0})     ${getStringLotteries(printModel.lotteries)}", paint)

        canvas.drawText(
            R_string(R.string.print_ases_and_maq)
                .replace("{{ases}}", "${printModel.sellerCode}")
                .replace("{{maq}}", getDeviceId()),
            paint
        )
        canvas.drawText("${R_string(R.string.print_check_equal)}     ${printModel.checkNumber}", paint)
        canvas.drawText("${R_string(R.string.print_sales_point_abr_equal)}  ${printModel.pdvCode}", paint)

        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 19f
        canvas.drawText("${R_string(R.string.print_header_superastro)}", paint)

        var posYDinamic = 340
        if ((printModel.lotteries?.count() ?: 1) >= 2){
            posYDinamic += 20
        }

        var posYNumbers = posYDinamic
        var posYticketPrizePlan = posYDinamic
        val incrementRow = 25
        val posXNumber = 0

        paint.typeface = Typeface.DEFAULT
        printModel.superastroList?.forEach {
            canvas.setY(posYNumbers)
            canvas.setX(posXNumber)
            canvas.drawText(it.number, paint)

            canvas.setY(posYNumbers)
            canvas.setX(posXNumber + 60)
            canvas.drawText("${it.value?.toInt() ?: 0}", paint)

            val select = it.zodiacSelected?.split("-")?.get(1)?.substring(0,3)
            canvas.setY(posYNumbers)
            canvas.setX(posXNumber + 130)
            canvas.drawText("${select ?: ""}", paint)
            posYNumbers += incrementRow
        }

        printModel.ticketPrizePlan?.split(";")?.forEach {
            canvas.setY(posYticketPrizePlan)
            canvas.setX(190)
            canvas.drawText("$it", paint)
            posYticketPrizePlan += incrementRow
        }

        paint.typeface = Typeface.DEFAULT_BOLD
        var posYValues = posYDinamic + 100
        canvas.setY(posYValues)
        canvas.setX(0)
        canvas.drawText("${R_string(R.string.print_pay_equal)}$${printModel.totalValuePaid?.toInt()}   ${R_string(R.string.text_iva_equal)}$${printModel.totalValueIva?.toInt()}   ${R_string(R.string.print_bet2_equal)}$${printModel.totalValueBetting?.toInt()}", paint)


        posYValues += 20
        canvas.setY(posYValues)
        canvas.setX(0)
        canvas.drawText("${printModel.draw}", paint)


        posYValues += 20
        canvas.setY(posYValues)
        paint.textSize = 22f
        canvas.drawText("                          ${printModel.digitChecking}", paint)

        paint.typeface = Typeface.DEFAULT
        posYValues += 21
        canvas.setY(posYValues)

        canvas.drawText("        PRODUCTO SUPERASTRO", paint)
        posYValues += 21
        canvas.setY(posYValues)
        canvas.setX(0)
        canvas.drawText("${printModel.ticketFooter}", paint)

        PrintManager.print(canvas, callback)
    }

    private fun getStringLotteries(lotteries: List<LotteryModel>?): String {
        val lotteryString = StringBuilder()
        lotteries?.forEachIndexed { index, lotteryModel ->
            if (index < lotteries.count() - 1){
                lotteryString.append("${lotteryModel.name} - ")
            }else{
                lotteryString.append("${lotteryModel.name}")
            }
        }
        return lotteryString.toString()
    }
}