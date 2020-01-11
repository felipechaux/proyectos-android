package co.com.pagatodo.core.data.print

import android.graphics.Typeface
import android.text.TextPaint
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.ProductBoxModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.data.print.PrintManager.Companion.setFontStyle
import co.com.pagatodo.core.data.print.bluetooth.PrintBluetoothManager
import java.util.*

class ConsultsPrint: BasePrint(), IConsultsPrint {
    override fun printConsult(
        products: List<ProductBoxModel>,
        saleTotal: String,
        stubs: String,
        sellerName: String,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        val bitmap= PrintManager.createBitmap()
        val canvas = PrintManager.createCanvas(bitmap)
        val paint = TextPaint()

        setFontStyle(paint, 2, false)

        val calendar = Calendar.getInstance()
        val dateFull = DateUtil.convertDateToStringFormat(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH, calendar.time)
        val timeFull = DateUtil.convertDateToStringFormat(DateUtil.StringFormat.HHMMSS, calendar.time)

        canvas.drawText("${R_string(R.string.print_date_label)} $dateFull",0F,100F, paint)
        canvas.drawText("${R_string(R.string.print_time_label)} $timeFull",0F,125F, paint)
        canvas.drawText("${R_string(R.string.print_seller_equal_upper)} $sellerName",0F,150F, paint)
        canvas.drawText("${R_string(R.string.print_machine_label)}  ${getDeviceId()}",0F,175F, paint)
        canvas.drawText("${R_string(R.string.print_stub_equal)}  $stubs",0F,200F, paint)
        canvas.drawText(R_string(R.string.print_version_label),0F,225F, paint)

        val posYDinamic = 300F
        canvas.drawText("                   ${R_string(R.string.print_currentbox_label)}",0F,posYDinamic, paint)

        val valuesList = createLabelWithData(products, saleTotal)
        val incrementPosYNumberAndPrizePlan = 30
        var sideCurrtentBoxPosY = posYDinamic + 25
        var sideCurrentBoxValuePosY = sideCurrtentBoxPosY

        paint.typeface = Typeface.DEFAULT
        val valueIndex = arrayListOf<Int>()
        valuesList.first.forEachIndexed { index, item ->
            when (item) {
                "P_JUEGOS_CHANCE", "SUPER P_JUEGOS_CHANCE", "P_JUEGOS_CHANCE MILLONARIO", "PAGO MILLONARIO", "TOTAL VENTA:" -> {
                    canvas.drawText(item,0F,sideCurrtentBoxPosY, paint)
                    sideCurrtentBoxPosY += incrementPosYNumberAndPrizePlan
                    valueIndex.add(index)
                }
            }
        }

        paint.typeface = Typeface.DEFAULT
        //add custom x,y position
        val posXPrizePlan = 240F
        for (index in 0 until valueIndex.count()) {
            canvas.drawText(valuesList.second[valueIndex[index]],posXPrizePlan,sideCurrentBoxValuePosY, paint)
            sideCurrentBoxValuePosY += incrementPosYNumberAndPrizePlan
        }

        PrintBluetoothManager.printBitmap(bitmap, callback)
    }

    private fun createLabelWithData(products: List<ProductBoxModel>, saleTotal: String): Pair<List<String>, List<String>> {
        val valuesName = arrayListOf<String>()
        val valuesData = arrayListOf<String>()
        valuesName.add(R_string(R.string.print_saletotal_label))
        valuesData.add(saleTotal)
        products.forEach { item ->
            item.name?.let { valuesName.add(it) }
            item.saleValue?.let { valuesData.add(it) }
        }
        return Pair(valuesName, valuesData)
    }
}