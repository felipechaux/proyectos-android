package co.com.pagatodo.core.data.print

import android.text.TextPaint
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.print.LotteriesPrintModel
import co.com.pagatodo.core.data.print.bluetooth.PrintBluetoothManager
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.formatValue

class LotteriesPrint: BasePrint(), ILotteriesPrint  {
    var numberFraction = 0

    override fun print(
        printModel: LotteriesPrintModel,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
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
        val bitmap= PrintManager.createBitmap()
        val canvas = PrintManager.createCanvas(bitmap)
        val paint = TextPaint()

        val currentFraction = (printModel.numbers ?: arrayListOf())[numberFraction]
        val stub = "${currentFraction.serie1}   ${currentFraction.serie2}"

        PrintManager.setFontStyle(paint, 2, false)
        canvas.drawText("            ${getCurrentDate(DateUtil.StringFormat.DMMMYYHOUR_SPLIT_WHITE_SPACE)}",0F,100F, paint)

        PrintManager.setFontStyle(paint, 3, true)
        canvas.drawText(printModel.lotteryName ?: "NA",0F,140F, paint)

        PrintManager.setFontStyle(paint, 2, false)
        canvas.drawText("${RUtil.R_string(R.string.print_seller_equal_upper)}  ${printModel.sellerCode}   ${RUtil.R_string(
            R.string.print_machine_label
        )} ${getDeviceId()}",0F, 165F, paint)

        canvas.drawText("${RUtil.R_string(R.string.print_stub_equal)}  ${stub}",0F,185F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_check_equal)}  ${printModel.digitChecked}",0F,205F, paint)

        PrintManager.setFontStyle(paint, 3, true)
        canvas.drawText("NUMERO      SERIE",0F,240F, paint)
        canvas.drawText("   ${currentFraction.number}             ${currentFraction.serie}",0F,270F, paint)

        PrintManager.setFontStyle(paint, 2, true)
        canvas.drawText("FRACCION             VALOR",0F,300F, paint)
        var value = ((printModel.value?.toInt() ?: 1) / (printModel.numbers?.size ?: 1))
        canvas.drawText(" ${currentFraction.fraction} de ${printModel.fraction}                    $${formatValue(value.toDouble())}",0F,320F, paint)

        PrintManager.setFontStyle(paint, 2, false)
        canvas.drawText("${RUtil.R_string(R.string.dialog_baloto_draw)} ${printModel.draw}   " +
                "${RUtil.R_string(R.string.text_label_date)} ${printModel.drawDate}",0F,350F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_time_draw)}   : ${printModel.drawHour}",0F,370F, paint)

        PrintManager.setFontStyle(paint, 2, true)
        canvas.drawText(RUtil.R_string(R.string.text_label_award),0F,400F, paint)

        PrintManager.setFontStyle(paint, 3, true)
        val prize = printModel.prize?.replace(".","")

        canvas.drawText("$prize",0F,430F, paint)

        PrintManager.setFontStyle(paint, 2, false)
        canvas.drawText(RUtil.R_string(R.string.print_guarded_super_health),0F,520F, paint)
        canvas.drawText(RUtil.R_string(R.string.print_awards_plan_valid),0F,540F, paint)

        PrintBluetoothManager.printBitmap(bitmap, callback)
    }

}