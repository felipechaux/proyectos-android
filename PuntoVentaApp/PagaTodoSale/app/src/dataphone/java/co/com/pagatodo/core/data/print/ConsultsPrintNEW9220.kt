package co.com.pagatodo.core.data.print

import android.graphics.Paint
import android.graphics.Typeface
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.ProductBoxModel
import co.com.pagatodo.core.data.print.PrintManager.Companion.addLineBreakHeaderPaper
import co.com.pagatodo.core.data.print.PrintManager.Companion.setFontStyle
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.formatValue
import com.pos.device.printer.PrintCanvas
import java.util.*

class ConsultsPrintNEW9220: BasePrint(), IConsultsPrint {

    override fun printConsult(products: List<ProductBoxModel>, saleTotal: String, stubs: String, sellerName: String, callback: (printerStatus: PrinterStatus) -> Unit?) {
        when(PrinterStatusInfo().getStatus()) {
            PrinterStatus.OK -> {

                val canvas = PrintCanvas()
                val paint = Paint()

                addLineBreakHeaderPaper(canvas, paint)
                PrintManager.addLineBreak(canvas, paint)
                setFontStyle(paint, 2, false)

                val calendar = Calendar.getInstance()
                val dateFull = DateUtil.convertDateToStringFormat(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH, calendar.time)
                val timeFull = DateUtil.convertDateToStringFormat(DateUtil.StringFormat.HHMMSS, calendar.time)

                canvas.drawText("${R_string(R.string.print_date_label)} $dateFull", paint)
                canvas.drawText("${R_string(R.string.print_time_label)} $timeFull", paint)
                canvas.drawText("${R_string(R.string.print_seller_equal_upper)} $sellerName", paint)
                canvas.drawText("${R_string(R.string.print_machine_label)}  ${getDeviceId()}", paint)
                canvas.drawText("${R_string(R.string.print_stub_equal)}  $stubs", paint)
                canvas.drawText("${R_string(R.string.print_version_label)}", paint)

                val posYDinamic = 275
                canvas.setY(posYDinamic)
                canvas.drawText("                   ${R_string(R.string.print_currentbox_label)}", paint)

                val valuesList = createDataList(products)
                val incrementPosYNumberAndPrizePlan = 30
                var sideCurrtentBoxPosY = posYDinamic + 20
                var sideCurrentBoxValuePosY = sideCurrtentBoxPosY
                canvas.setY(sideCurrtentBoxPosY)

                paint.typeface = Typeface.DEFAULT
                val valueIndex = arrayListOf<Int>()
                valuesList.first.forEachIndexed { index, item ->
                    when (item) {
                        "CHANCE", "SUPER CHANCE", "PAGO MILLONARIO", "RIFAS", "TOTAL VENTA:" -> {
                            canvas.setY(sideCurrtentBoxPosY)
                            canvas.drawText(item, paint)
                            sideCurrtentBoxPosY += incrementPosYNumberAndPrizePlan
                            valueIndex.add(index)
                        }
                    }
                }
                paint.typeface = Typeface.DEFAULT
                val posXPrizePlan = 240
                for (index in 0 until valueIndex.count()) {
                    canvas.setX(posXPrizePlan)
                    canvas.setY(sideCurrentBoxValuePosY)
                    canvas.drawText(valuesList.second[valueIndex[index]], paint)
                    sideCurrentBoxValuePosY += incrementPosYNumberAndPrizePlan
                }
                PrintManager.addLineBreak(canvas, paint, 7)
                PrintManager.print(canvas, callback)
            }
        }
    }

    private fun createDataList(products: List<ProductBoxModel>): Pair<List<String>, List<String>> {
        val valuesName = arrayListOf<String>()
        val valuesData = arrayListOf<String>()
        var total = 0
        products.forEach { item ->
            item.name?.let { valuesName.add(it) }
            item.saleValue?.let { valuesData.add(it) }
            when (item.name) {
                "CHANCE", "SUPER CHANCE", "PAGO MILLONARIO", "RIFAS", "TOTAL VENTA:" -> {
                    item.saleValue?.let { total += it.replace("$", "")?.replace(".", "")?.toInt() }
                }
            }
        }
        valuesName.add(R_string(R.string.print_saletotal_label))
        valuesData.add("$${formatValue(total.toDouble())}")
        return Pair(valuesName, valuesData)
    }
}