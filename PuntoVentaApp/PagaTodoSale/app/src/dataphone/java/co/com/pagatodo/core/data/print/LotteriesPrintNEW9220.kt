package co.com.pagatodo.core.data.print

import android.graphics.Paint
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.VirtualLotteryModel
import co.com.pagatodo.core.data.print.PrintManager.Companion.addLineBreak
import co.com.pagatodo.core.data.print.PrintManager.Companion.addLineBreakHeaderPaper
import co.com.pagatodo.core.data.print.PrintManager.Companion.setFontStyle
import co.com.pagatodo.core.util.DateUtil
import com.pos.device.printer.PrintCanvas
import co.com.pagatodo.core.data.model.print.LotteriesPrintModel
import co.com.pagatodo.core.data.model.response.ResponseCheckNumberLotteryModel
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.formatValue
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class LotteriesPrintNEW9220: BasePrint(), ILotteriesPrint {
    override fun printFractionsAvailable(
        printModel: ResponseCheckNumberLotteryModel,
        model: VirtualLotteryModel,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var numberFraction = 0

    override fun print(printModel: LotteriesPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        if (numberFraction < printModel.numbers?.size ?: 0){
            printFraction(printModel, numberFraction, isRetry){
                numberFraction += 1
                print(printModel, isRetry,callback)
            }
        }else{
            callback(PrinterStatus.OK)
        }
    }

    private fun printFraction(printModel: LotteriesPrintModel, numberFraction: Int, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?){
        val canvas = PrintCanvas()
        val paint = Paint()

        val currentFraction = (printModel.numbers ?: arrayListOf())[numberFraction]
        val stub = "${currentFraction.serie1}   ${currentFraction.serie2}"

        addLineBreakHeaderPaper(canvas, paint)
        addLineBreak(canvas, paint)

        setFontStyle(paint, 2, false)
        canvas.drawText("            ${getCurrentDate(DateUtil.StringFormat.DMMMYYHOUR_SPLIT_WHITE_SPACE)}", paint)
        addLineBreak(canvas, paint)

        setFontStyle(paint, 3, true)
        canvas.drawText(printModel.lotteryName, paint)

        setFontStyle(paint, 2, false)
        canvas.drawText("${R_string(R.string.print_seller_equal_upper)}  ${printModel.sellerCode}   ${R_string(R.string.print_machine_label)}${getDeviceId()}", paint)

        canvas.drawText("${R_string(R.string.print_stub_equal)}  ${stub} ${if(isRetry){
            R_string(R.string.print_retry_icon)}else{""}}", paint)
        canvas.drawText("${R_string(R.string.print_check_equal)}  ${printModel.digitChecked}", paint)
        addLineBreak(canvas, paint)

        setFontStyle(paint, 3, true)
        canvas.drawText("NUMERO      SERIE", paint)
        canvas.drawText("   ${currentFraction.number}             ${currentFraction.serie}", paint)

        setFontStyle(paint, 2, true)
        canvas.drawText("FRACCION             VALOR", paint)
        canvas.drawText(" ${currentFraction.fraction} de ${printModel.fraction}                    $${formatValue((printModel.fractionValue ?: "").toDouble())}", paint)

        setFontStyle(paint, 2, false)
        canvas.drawText("${R_string(R.string.dialog_baloto_draw)} ${printModel.draw}   ${R_string(R.string.text_label_date)} ${printModel.drawDate}", paint)
        canvas.drawText("${R_string(R.string.print_time_draw)}   : ${printModel.drawHour}", paint)

        setFontStyle(paint, 2, true)
        canvas.drawText("${R_string(R.string.text_label_award)}", paint)
        setFontStyle(paint, 3, true)

        val value = printModel.prize?.replace(".","")

        canvas.drawText("${value}", paint)
        addLineBreak(canvas, paint, 2)

        setFontStyle(paint, 2, false)
        canvas.setY(500)
        canvas.drawText("${R_string(R.string.print_guarded_super_health)}", paint)
        canvas.setY(520)
        canvas.drawText("${R_string(R.string.print_awards_plan_valid)}", paint)
        addLineBreak(canvas, paint, 3)
        PrintManager.print(canvas, callback)
    }

}