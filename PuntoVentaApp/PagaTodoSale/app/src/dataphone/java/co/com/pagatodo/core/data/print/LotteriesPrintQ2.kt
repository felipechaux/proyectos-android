package co.com.pagatodo.core.data.print

import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.VirtualLotteryModel
import co.com.pagatodo.core.data.model.print.LotteriesPrintModel
import co.com.pagatodo.core.data.model.response.LotteryNumberModel
import co.com.pagatodo.core.data.model.response.ResponseCheckNumberLotteryModel
import co.com.pagatodo.core.util.*
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import com.cloudpos.DeviceException
import com.wizarpos.htmllibrary.PrinterHtmlListener

class LotteriesPrintQ2 : BasePrint(), ILotteriesPrint {

    var str:String=""

    override fun printFractionsAvailable(printModel: ResponseCheckNumberLotteryModel, model: VirtualLotteryModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        printFractions(printModel, model, isRetry, callback)
    }


    override fun print(
        printModel: LotteriesPrintModel,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
            printFraction(printModel, isRetry, callback)
    }


    private fun printFraction(
        printModel: LotteriesPrintModel,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {


        var numberFraction = 0
        val sbPrintData = StringBuilder()

        str="<br>"
        for (index in 0..(printModel.numbers?.size!!)-1){


            sbPrintData.append(gerPrintData(printModel, numberFraction, isRetry))
            numberFraction += 1

            if (numberFraction > 1) {
                str="<br><br>"
            }

        }
        if (numberFraction > 1) {
            //billete completo
            executeHtmlPrintQ2(sbPrintData.toString(), 0, 30,0, callback)

        } else {
            executeHtmlPrintQ2(sbPrintData.toString(), 0, 0,0, callback)
        }
        sbPrintData.clear()



    }

    private fun gerPrintData(
        printModel: LotteriesPrintModel,
        numberFraction: Int,
        isRetry: Boolean
    ): String? {

        val currentFraction = (printModel.numbers ?: arrayListOf())[numberFraction]
        val stub = "${currentFraction.serie1}   ${currentFraction.serie2}"

        return "<style>" +
                    "p.small {line-height: 0.2;}" +
                    "p.big {line-height: 0.8;}" +
                "</style>" +
                this.str +
                "<p align=\"center\"><br><br><br><font face=\"verdana\" size=\"4\">${getCurrentDate(DateUtil.StringFormat.DMMMYYHOUR_SPLIT_WHITE_SPACE)}<br></font><br>" +
                "<p class=\"small\">" +
                    "<font face=\"verdana\" size=\"5\">" +
                        "<b>${printModel.lotteryName}</b>" +
                    "</font>" +
                "</p>" +
                "<p class=\"small\">${R_string(R.string.print_seller_equal_upper)}  ${printModel.sellerCode}   ${R_string(
                    R.string.print_machine_label
                    )}${getDeviceId()}</p>" +
                "<p class=\"small\">${R_string(R.string.print_stub_equal)}  ${stub} ${if (isRetry) {
                    R_string(R.string.print_retry_icon)
                    } else {
                    ""
                    }}" +
                "</p>" +
                "<p class=\"small\">${R_string(R.string.print_check_equal)}  ${printModel.digitChecked}</p>" +
                "<table cellpadding=\"0\" style = \"width:100%\">" +
                    "<tr valign=\"top\">" +
                        "<th  align=\"left\">" +
                        "<font face=\"verdana\" size=\"5\">NUMERO</font>" +
                        "</th>" +
                        "<th  align=\"left\">" +
                        "<font face=\"verdana\" size=\"5\">SERIE</font>" +
                        "</th>" +
                    "</tr>" +
                    "<tr valign=\"top\">" +
                        "<td align=\"left\" valign=\"top\" width=\"50.0%\">" +
                            "<font face=\"verdana\" size=\"5\"><b>${currentFraction.number}</b></font>" +
                        "</td>" +
                        "<td align=\"left\" valign=\"top\" width=\"50.0%\">" +
                            "<font face=\"verdana\" size=\"5\"><b>${currentFraction.serie}</b></font>" +
                        "</td>" +
                    "</tr>" +
                    "<tr valign=\"top\">" +
                        "<td align=\"left\" valign=\"top\" width=\"50.0%\">" +
                            "<font face=\"verdana\" size=\"4\"><b>FRACCIÓN</b></font>" +
                        "</td>" +
                        "<td align=\"left\" valign=\"top\" width=\"50.0%\">" +
                            "<font face=\"verdana\" size=\"4\"><b>VALOR</b></font>" +
                        "</td>" +
                    "</tr>" +
                    "<tr valign=\"top\">" +
                        "<td align=\"left\" valign=\"top\" width=\"50.0%\">" +
                            "<font face=\"verdana\" size=\"4\"><b>${currentFraction.fraction} de ${printModel.fraction}</b></font>" +
                        "</td>" +
                        "<td align=\"left\" valign=\"top\" width=\"50.0%\">" +
                            "<font face=\"verdana\" size=\"4\">" +
                                "<b>" +
                                    "\$${formatValue((printModel.fractionValue ?: "").toDouble())}" +
                                "</b>" +
                            "</font>" +
                        "</td>" +
                    "</tr>" +
                "</table>" +
                "<font face=\"verdana\" size=\"4\">" +
                    "<p class=\"small\">${R_string(R.string.dialog_baloto_draw)} ${printModel.draw} ${R_string(R.string.text_label_date)} ${printModel.drawDate}</p>" +
                    "<p class=\"small\">${R_string(R.string.print_time_draw)}   : ${printModel.drawHour}</p>" +
                    "<p class=\"small\"><b>${R_string(R.string.text_label_award)}</b></p>" +
                    "<p class=\"small\"><font face=\"verdana\" size=\"5\"><b>${printModel.prize}</b></font></p>" +
                    "<p class=\"small\">${R_string(R.string.print_guarded_super_health)}</p>" +
                    "<p class=\"small\">${R_string(R.string.print_awards_plan_valid)}</p>" +
                "<br></br><br></br>"+
                "</font>"

    }

    fun printFractions(printModel: ResponseCheckNumberLotteryModel, model: VirtualLotteryModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?){

        val  numberFraction = 0
        val sbPrintData = StringBuilder()
        sbPrintData.append(getFractionsPrint(printModel, model, numberFraction, isRetry))

        executeHtmlPrintQ2(sbPrintData.toString(),0,20,0,callback)

    }

    private fun getFractionsPrint(printModel: ResponseCheckNumberLotteryModel, model: VirtualLotteryModel, numberFraction: Int, isRetry: Boolean): String?{
        val userCode : String = SharedPreferencesUtil.getPreference(R_string(R.string.shared_key_seller_code))
        val terminalCode: String = DeviceUtil.getSerialNumber()?:""

        val sbBody = StringBuilder()

        sbBody.append("<style>\"p.small {line-height: 0.2;} p.big {line-height: 0.8;}</style>")
        sbBody.append("<p align=\"center\"><font face=\"verdana\" size=\"4\">${"PLANILLA ASIGNACION DE LOTERIAS"}</font></p>")
        sbBody.append("<font size=\"3\">${getCurrentDate(DateUtil.StringFormat.DMMMYYHOUR_SPLIT_WHITE_SPACE)}</font><br>")
        sbBody.append("<font size=\"3\">${R_string(R.string.print_seller_equal_upper)}   ${userCode}   ${R_string(R.string.print_machine_label)}${terminalCode}</font><br>")
        sbBody.append("<font size=\"3\">${R_string(R.string.text_lottery)}  ${model.fullName}</font><br>")
        sbBody.append("<font size=\"3\">${R_string(R.string.print_lottery_pdv)}  ${model.draw}</font><br>")
        sbBody.append("<font size=\"3\">${R_string(R.string.print_lottery_price_fraction)} ${"$"}${formatValue(model.fractionValue!!.toDouble())}</font><br>")
        sbBody.append("<table cellpadding=\"0\" style = \"width:100%\">")
        sbBody.append("<tr valign=\"top\">")
        sbBody.append("<th  align=\"left\">")
        sbBody.append("<font face=\"verdana\" size=\"3\">FRACCIONES<br> DISPONIBLES</font>")
        sbBody.append("</th>")
        sbBody.append("<th  align=\"center\">")
        sbBody.append("<font face=\"verdana\" size=\"3\">SERIE</font>")
        sbBody.append("</th>")
        sbBody.append("<th  align=\"center\">")
        sbBody.append("<font face=\"verdana\" size=\"3\">NUMERO</font>")
        sbBody.append("</th>")
        sbBody.append("</tr>")

        printModel.tickets?.forEach{
            sbBody.append("<tr valign=\"top\">")
            sbBody.append("<td align=\"center\" valign=\"top\" width=\"50.0%\">")
            sbBody.append("${it.fraction}")
            sbBody.append("</td>")
            sbBody.append("<td align=\"center\" valign=\"top\" width=\"50.0%\">")
            sbBody.append("${it.serie}")
            sbBody.append("</td>")
            sbBody.append("<td align=\"center\" valign=\"top\" width=\"50.0%\">")
            sbBody.append("${it.number}")
            sbBody.append("</td>")
            sbBody.append("</tr>")
        }


        val sumFractionSlopes = printModel.tickets?.map { it.fraction?.toInt() }!!.sumBy { it!! }

        sbBody.append("</table>")
        sbBody.append("<br><br><br>")
        sbBody.append("<font size=\"3\">${R_string(R.string.print_fractions_slopes)}   ${sumFractionSlopes} </font><br>")
        sbBody.append("<br><br><br>")
        return sbBody.toString()
    }
}


