package co.com.pagatodo.core.data.print

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.PagaTodoApplication.Companion.format
import co.com.pagatodo.core.PagaTodoApplication.Companion.printerDevice
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.print.SuperAstroPrintModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import com.cloudpos.DeviceException
import com.cloudpos.printer.Format.*
import com.wizarpos.htmllibrary.PrinterHtmlListener
import co.com.pagatodo.core.R

class SuperAstroPrintQ2 : BasePrint(), ISuperAstroPrint {

    var footerDocumento = ""

    override fun print(
        printModel: SuperAstroPrintModel,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        if (openPrinter() != 0) {
            callback(PrinterStatus.BUSY)
            return
        }
        try {
            addLineBreak(3)

            var arrayHeader =
                printModel.textHeader?.replace("\\\\n", "\n")?.replace("\\n", "\n")?.split("\n")

            format?.setParameter(FORMAT_ALIGN, FORMAT_ALIGN_LEFT)
            format?.setParameter(FORMAT_DENSITY, FORMAT_DENSITY_DARK)
            format?.setParameter(FORMAT_FONT_BOLD, FORMAT_FONT_VAL_TRUE)
            format?.setParameter(FORMAT_FONT_SIZE, FORMAT_FONT_SIZE_SMALL)

            arrayHeader?.forEachIndexed { index, astroHeader ->
                if (index <= 3) {
                    if (index == 3) {
                        printerDevice.printText(format, "${astroHeader.toLowerCase()}\n")
                    } else {
                        printerDevice.printText(format, "${astroHeader}\n")

                    }
                }

            }

            val currentDate = getCurrentDate(DateUtil.StringFormat.DDMMYYHOURM_SPLIT_WHITE_SPACE)
            printerDevice.printText(
                format,
                "${R_string(R.string.print_date_equal)} ${currentDate}"
            )
            printerDevice.printText(
                format,
                "${R_string(R.string.print_draw_date_equal)} ${currentDate}"
            )
            printerDevice.printText(
                format, "${R_string(R.string.print_serie)} :   ${printModel.stub}${if (isRetry) {
                    R_string(R.string.print_retry_icon)
                } else {
                    ""
                }}"
            )
            printerDevice.printText(
                format, "${R_string(R.string.print_draw_text)}(${printModel.lotteries?.count()
                    ?: 0})  ${getStringLotteries(printModel.lotteries)}"
            )

            printerDevice.printText(
                format, R_string(R.string.print_ases_and_maq)
                    .replace("{{ases}}", "${printModel.sellerCode}")
                    .replace("{{maq}}", getDeviceId())
            )
            printerDevice.printText(
                format,
                "${R_string(R.string.print_check_equal)}   ${printModel.checkNumber}"
            )
            printerDevice.printText(
                format,
                "${R_string(R.string.print_sales_point_abr_equal)}  ${printModel.pdvCode}\n"
            )

            val htmlContent = "<pre>" + printGame(printModel) + "</pre>"


            try {
                printerDevice.printHTML(
                    PagaTodoApplication.getAppContext(),
                    htmlContent,
                    object : PrinterHtmlListener {
                        override fun onGet(bitmap: Bitmap, i: Int) {}
                        override fun onFinishPrinting(result: Int) {

                            when (result) {
                                PrinterHtmlListener.PRINT_SUCCESS -> {
                                    printFooter(printModel)
                                    if (printModel.superastroList!!.size > 2)
                                        lpt_feed_dots(68)
                                    else
                                        lpt_feed_dots(88)

                                    executeCallBack(PrinterStatus.OK, callback)
                                }
                                PrinterHtmlListener.PRINT_ERROR ->
                                    executeCallBack(PrinterStatus.ERROR, callback)
                            }


                        }
                    })
            } catch (e: Exception) {
                e.printStackTrace()
                executeCallBack(PrinterStatus.ERROR, callback)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            executeCallBack(PrinterStatus.ERROR, callback)
        }
    }

    private fun printFooter(printModel: SuperAstroPrintModel) {

        format?.setParameter(FORMAT_ALIGN, FORMAT_ALIGN_LEFT)
        format?.setParameter(FORMAT_DENSITY, FORMAT_DENSITY_DARK)
        format?.setParameter(FORMAT_FONT_BOLD, FORMAT_FONT_VAL_TRUE)
        format?.setParameter(FORMAT_FONT_SIZE, FORMAT_FONT_SIZE_SMALL)

        printerDevice.printText(
            format,
            "$footerDocumento"
        )
        printerDevice.printText(
            format,
            "${R_string(R.string.print_pay_equal)}$${printModel.totalValuePaid?.toInt()}  ${R_string(
                R.string.text_iva_equal
            )}$${printModel.totalValueIva?.toInt()}  ${R_string(R.string.print_bet2_equal)}$${printModel.totalValueBetting?.toInt()}"
        )
        printerDevice.printText(
            format,
            "${getStringLotteries(printModel.lotteries, true)}"
        )

        format?.setParameter(FORMAT_ALIGN, FORMAT_ALIGN_CENTER)
        printerDevice.printText(
            format,
            "${printModel.digitChecking}"
        )
        printerDevice.printText(
            format,
            "PRODUCTO SUPERASTRO"
        )

        format?.setParameter(FORMAT_ALIGN, FORMAT_ALIGN_LEFT)
        printerDevice.printText(
            format,
            "${printModel.ticketFooter}"
        )

    }

    private fun printGame(printModel: SuperAstroPrintModel): Any? {
        val sbBody = StringBuilder()
        var br = ""
        sbBody.append("<div style = \"width:52%; margin:-10px; float: left\"><table style = \"width:100%\">")
        sbBody.append("<tr>")
        sbBody.append("<th align=\"left\"><font face=\"verdana\">NUME</font></th>")
        sbBody.append("<th align=\"left\"><font face=\"verdana\">VALOR</font></th>")
        sbBody.append("<th align=\"left\"><font face=\"verdana\">ASTRO</font></th>")
        sbBody.append("</tr>")

        for (i in printModel.superastroList!!.size..3) {
            br += "<br>----"
        }
        printModel.superastroList?.forEachIndexed { index, astro ->
            sbBody.append("<tr>")
            sbBody.append("<td align=\"left\" valign=\"top\" width=\"17.3%\"><font face=\"verdana\" size=\"4\"><strong>")
            sbBody.append(astro.number)
            if (index + 1 == printModel.superastroList!!.size) {
                sbBody.append("$br")
            }
            sbBody.append("</strong></font></td>")
            sbBody.append("<td align=\"left\" valign=\"top\" width=\"17.3%\"><font face=\"verdana\" size=\"4\"><strong>")
            sbBody.append("${astro.value?.toInt() ?: 0}")
            if (index + 1 == printModel.superastroList!!.size) {
                sbBody.append("$br")
            }
            sbBody.append("</strong></font></td>")
            sbBody.append("<td align=\"left\" valign=\"top\" width=\"17.3%\"><font face=\"verdana\" size=\"4\"><strong>")
            val select = astro.zodiacSelected?.split("-")?.get(1)?.substring(0, 3)
            sbBody.append("${select ?: ""}")
            if (index + 1 == printModel.superastroList!!.size) {
                sbBody.append("$br")
            }
            sbBody.append("</strong></font></td>")
            sbBody.append("</tr>")
        }
        sbBody.append("</table></div>")
        sbBody.append("<div style = \"width:48%; margin:-7px; float: right\">")
        sbBody.append("<font face=\"verdana\" size=\"3\"><strong>")

        printModel.ticketPrizePlan?.split(";")?.forEachIndexed { index, z ->

            if (index != 3) {
                sbBody.append(" <br>$z")
            } else {
                footerDocumento = z
                sbBody.append(" <br>")
            }

        }
        sbBody.append("</strong></font></div>")

        println(TextUtils.htmlEncode(sbBody.toString()))
        return sbBody.toString()

    }

    private fun getStringLotteries(
        lotteries: List<LotteryModel>?,
        isFooter: Boolean = false
    ): String {
        val lotteryString = StringBuilder()
        lotteries?.forEachIndexed { index, lotteryModel ->
            if (index < lotteries.count() - 1) {
                if (isFooter) {
                    lotteryString.append(
                        "${lotteryModel.name} ${lotteryModel.hour?.replace(
                            ":",
                            ""
                        )} - "
                    )
                } else {
                    lotteryString.append("${lotteryModel.fullName} - ")
                }
            } else {
                if (isFooter) {
                    lotteryString.append(
                        "${lotteryModel.name} ${lotteryModel.hour?.replace(
                            ":",
                            ""
                        )}"
                    )
                } else {
                    lotteryString.append("${lotteryModel.fullName}")
                }
            }


        }
        return lotteryString.toString().toUpperCase()
    }
}