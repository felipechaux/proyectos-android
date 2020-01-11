package co.com.pagatodo.core.data.print

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.ProductBoxModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.formatValue
import com.cloudpos.DeviceException
import com.wizarpos.htmllibrary.PrinterHtmlListener
import java.util.*

class ConsultsPrintQ2 : BasePrint(), IConsultsPrint {

    override fun printConsult(
        products: List<ProductBoxModel>,
        saleTotal: String,
        stubs: String,
        sellerName: String,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        val htmlContent = gerPrintData(products, stubs, sellerName)

        executeHtmlPrintQ2(htmlContent,  13, 0,0, callback)

    }

    private fun gerPrintData(
        products: List<ProductBoxModel>,
        stubs: String,
        sellerName: String
    ): String {

        val sbPrintData = StringBuilder()
        val calendar = Calendar.getInstance()
        val dateFull =
            DateUtil.convertDateToStringFormat(
                DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH,
                calendar.time
            )
        val timeFull =
            DateUtil.convertDateToStringFormat(DateUtil.StringFormat.HHMMSS, calendar.time)

        val valuesList = createDataList(products)
        val valueIndex = arrayListOf<Int>()
        var ind = 0
        valuesList.first.forEachIndexed { index, item ->
            when (item) {
                "CHANCE", "SUPER CHANCE", "PAGO MILLONARIO", "RIFAS", "TOTAL VENTA:" -> {
                    valueIndex.add(index)

                    sbPrintData.append("<tr valign=\"top\">")
                    sbPrintData.append("<td align=\"left\" valign=\"top\" width=\"70.0%\">")
                    sbPrintData.append("<font face=\"verdana\" size=\"4\"><b>")
                    sbPrintData.append(item)
                    sbPrintData.append("</b></font>")
                    sbPrintData.append("</td>")
                    sbPrintData.append("<td align=\"left\" valign=\"top\" width=\"70.0%\">")
                    sbPrintData.append("<font face=\"verdana\" size=\"4\"><b>")
                    sbPrintData.append(valuesList.second[valueIndex[ind++]])
                    sbPrintData.append("</b></font>")
                    sbPrintData.append("</td>")
                    sbPrintData.append("</tr>")
                }
            }
        }

        return "<style>" +
                "p.small {line-height: 0.1;}" +
                "p.big {line-height: 1.8;}" +
                "</style>" +
                "<br><br><br>" +
                "<font face=\"verdana\" size=\"4\">" +
                "<p class=\"small\">${RUtil.R_string(R.string.print_date_label)} $dateFull</p>" +
                "<p class=\"small\">${RUtil.R_string(R.string.print_time_label)} $timeFull</p>" +
                "<p class=\"small\">${RUtil.R_string(R.string.print_seller_equal_upper)} $sellerName</p>" +
                "<p class=\"small\">${RUtil.R_string(R.string.print_machine_label)}  ${getDeviceId()}</p>" +
                "<p class=\"small\">${RUtil.R_string(R.string.print_stub_equal)}  $stubs</p>" +
                "<p class=\"small\">${RUtil.R_string(R.string.print_version_label)}</p>" +
                "<p  align=\"center\" class=\"big\">${RUtil.R_string(R.string.print_currentbox_label)}</p>" +
                "</font>" +
                "<table cellpadding=\"0\" style = \"width:100%\">" +
                sbPrintData +
                "</table>"

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
        valuesName.add(RUtil.R_string(R.string.print_saletotal_label))
        valuesData.add("$${formatValue(total.toDouble())}")
        return Pair(valuesName, valuesData)
    }
}