package co.com.pagatodo.core.data.print

import android.graphics.Typeface
import android.os.Build
import android.text.StaticLayout
import android.text.TextPaint
import androidx.annotation.RequiresApi
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.print.SuperAstroPrintModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.data.print.PrintManager.Companion.createBitmap
import co.com.pagatodo.core.data.print.PrintManager.Companion.createCanvas
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.data.print.PrintManager.Companion.setFontStyle
import co.com.pagatodo.core.data.print.bluetooth.PrintBluetoothManager
import java.lang.StringBuilder

class SuperAstroPrint: BasePrint(), ISuperAstroPrint {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun print(
        printModel: SuperAstroPrintModel,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        val bitmap= createBitmap()
        val canvas = createCanvas(bitmap)
        val paint = TextPaint()

        setFontStyle(paint, 2, false)
        var positionYHeader = 100F

        paint.textSize = 19f

        val textHeader = printModel.textHeader?.split("\\\\n") ?: arrayListOf()
        canvas.drawText(textHeader[0],0F, positionYHeader, paint)

        val staticLayoutHeader = StaticLayout.Builder
            .obtain(textHeader[1], 0, textHeader[1].length, paint, canvas.width)
            .build()

        canvas.translate(0F, positionYHeader)
        staticLayoutHeader.draw(canvas)

        positionYHeader = (staticLayoutHeader.lineCount * 25) + 20F

        canvas.drawText(textHeader[2],0F, positionYHeader, paint)
        positionYHeader += 25

        setFontStyle(paint, 2, false)
        canvas.drawText("${RUtil.R_string(R.string.print_date_equal)} ${getCurrentDate(DateUtil.StringFormat.DDMMYYHOUR_SPLIT_WHITE_SPACE)}",0F, positionYHeader, paint)

        val textDateDraw = "${RUtil.R_string(R.string.print_draw_date_equal)} ${printModel.drawDate}"
        val staticLayoutDates = StaticLayout.Builder
            .obtain(textDateDraw, 0, textDateDraw.length, paint, canvas.width)
            .build()

        canvas.translate(0F, positionYHeader)
        staticLayoutDates.draw(canvas)

        positionYHeader = (40 * staticLayoutDates.lineCount).toFloat()
        if (staticLayoutDates.lineCount == 1){
            positionYHeader += 10
        }else{
            positionYHeader -= 10
        }
        canvas.drawText("${RUtil.R_string(R.string.print_serie)} :     ${printModel.stub}${if(isRetry){
            RUtil.R_string(R.string.print_retry_icon)
        }else{""}}",0F,positionYHeader, paint)

        positionYHeader += 25
        paint.textSize = 20F
        canvas.drawText("${RUtil.R_string(R.string.print_draw_text)}(${printModel.lotteries?.count() ?: 0})     " +
                getStringLotteries(printModel.lotteries),0F,positionYHeader, paint)

        positionYHeader += 25
        canvas.drawText(
            RUtil.R_string(R.string.print_ases_and_maq)
                .replace("{{ases}}", "${printModel.sellerCode}")
                .replace("{{maq}}", getDeviceId()),0F,positionYHeader, paint
        )
        positionYHeader += 25
        setFontStyle(paint, 2, false)
        canvas.drawText("${RUtil.R_string(R.string.print_check_equal)}     ${printModel.checkNumber}",0F,positionYHeader, paint)
        positionYHeader += 25
        paint.textSize = 20F
        canvas.drawText("${RUtil.R_string(R.string.print_sales_point_abr_equal)}  ${printModel.pdvCode}",0F,positionYHeader, paint)

        positionYHeader += 25
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 19f
        canvas.drawText(RUtil.R_string(R.string.print_header_superastro),0F,positionYHeader, paint)

        val posYDinamic = positionYHeader + 25

        var posYNumbers = posYDinamic
        var posYticketPlanPrize = posYDinamic
        val incrementRow = 22
        val posXNumber = 0F

        printModel.superastroList?.forEach {
            canvas.drawText(it.number ?: "",posXNumber,posYNumbers, paint)
            canvas.drawText("${it.value?.toInt() ?: 0}",(posXNumber + 65),posYNumbers, paint)

            val select = it.zodiacSelected?.split("-")?.get(1)?.substring(0,3)
            canvas.drawText(select ?: "",(posXNumber + 130),posYNumbers, paint)
            posYNumbers += incrementRow
        }

        printModel.ticketPrizePlan?.split(";")?.forEach {
            canvas.drawText(it,190F,posYticketPlanPrize, paint)
            posYticketPlanPrize += incrementRow
        }

        paint.typeface = Typeface.DEFAULT_BOLD
        var posYValues = posYDinamic + 90
        canvas.drawText("${RUtil.R_string(R.string.print_pay_equal)}$${printModel.totalValuePaid?.toInt()} " +
                "${RUtil.R_string(R.string.text_iva_equal)}$${printModel.totalValueIva?.toInt()} " +
                "${RUtil.R_string(R.string.print_bet2_equal)}$${printModel.totalValueBetting?.toInt()}",0F,posYValues, paint)

        posYValues += 20
        canvas.drawText("${printModel.draw}",0F,posYValues, paint)

        paint.textSize = 22f
        posYValues += 20
        canvas.drawText("${printModel.digitChecking}",60F,posYValues, paint)

        setFontStyle(paint, 2, false)
        posYValues += 22
        canvas.drawText(" PRODUCTO SUPERASTRO",60F,posYValues, paint)

        paint.textSize = 19f
        val staticLayout = StaticLayout.Builder
            .obtain(printModel.ticketFooter ?: "", 0, printModel.ticketFooter?.length ?: 0, paint, canvas.width)
            .build()

        canvas.translate(0F, posYValues)
        staticLayout.draw(canvas)

        PrintBluetoothManager.printBitmap(bitmap, callback)
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