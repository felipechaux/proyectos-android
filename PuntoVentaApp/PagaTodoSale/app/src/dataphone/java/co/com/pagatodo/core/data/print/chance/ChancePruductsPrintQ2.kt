package co.com.pagatodo.core.data.print.chance

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.PagaTodoApplication.Companion.printerDevice
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.ChanceModel
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.ModeValueModel
import co.com.pagatodo.core.data.model.SuperchanceModel
import co.com.pagatodo.core.data.model.print.BasePrintModel
import co.com.pagatodo.core.data.print.BasePrint
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.formatValue
import com.cloudpos.DeviceException
import com.wizarpos.htmllibrary.PrinterHtmlListener
import com.wizarpos.htmllibrary.PrinterHtmlListener.PRINT_ERROR
import com.wizarpos.htmllibrary.PrinterHtmlListener.PRINT_SUCCESS

class ChancePruductsPrintQ2 : BasePrint(), IChanceProductsPrint {

    private var maxRowInChanceGame = 6

    /**
     * Método utlizado para realizar la impresión del producto chance
     */
    override fun printChance(
        totalValueBetting: Double,
        totalValuePaid: Double,
        totalValueOverloaded: Double,
        totalValueIva: Double,
        raffleDate: String,
        raffleHour: String,
        lotteries: List<LotteryModel>,
        chances: List<ChanceModel>,
        iva: Int,
        sellerName: String,
        uniqueSerial: String,
        digitChecking: String,
        stubs: String,
        maxRows: Int,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        var htmlContent = ""

        try {
            // Obtención de las loterías en un string con el formato de impresión
            val stringLotteries = getLotteriesWithPrintFormat(lotteries, 4)
            val stringData = "${raffleDate} ${raffleHour}"
            val date = DateUtil.convertStringToDateFormat(
                DateUtil.StringFormat.DDMMYY_HHMMSS_SPLIT_DASH,
                stringData
            )

            val sbPlay = StringBuilder()
            var i = 0
            do {
                sbPlay.append("<tr valign=\"top\"><td style=\"padding:0px\" valign=\"top\" width=\"25%\"><font face=\"verdana\" size=\"5\"><strong>")
                sbPlay.append((chances[i].number ?: "").padStart(4, '*'))
                sbPlay.append("</strong></font></td>")
                sbPlay.append("<td style=\"padding:0px\" align=\"center\" valign=\"top\" width=\"18.5%\"><font face=\"verdana\" size=\"4\">")
                sbPlay.append(chances[i].direct?.trim().let { if (it.isNullOrEmpty()) "0" else it })
                sbPlay.append("</font></td>")
                sbPlay.append("<td style=\"padding:0px\" align=\"center\" valign=\"top\" width=\"18.5%\"><font face=\"verdana\" size=\"4\">")
                sbPlay.append(chances[i].combined?.trim().let { if (it.isNullOrEmpty()) "0" else it })
                sbPlay.append("</font></td>")
                sbPlay.append("<td style=\"padding:0px\" align=\"center\" valign=\"top\" width=\"18.5%\"><font face=\"verdana\" size=\"4\">")
                sbPlay.append(chances[i].paw?.trim().let { if (it.isNullOrEmpty()) "0" else it })
                sbPlay.append("</font></td>")
                sbPlay.append("<td style=\"padding:0px\" align=\"center\" valign=\"top\" width=\"18.5%\"><font face=\"verdana\" size=\"4\">")
                sbPlay.append(chances[i].nail?.trim().let { if (it.isNullOrEmpty()) "0" else it })
                sbPlay.append("</font></td></tr>")
                i++

            } while (i < chances.count())
            if (chances.count() < maxRows) {
                val rest = maxRows - chances.count()
                for (index in 1..rest) {
                    sbPlay.append("<tr valign=\"top\">")
                    sbPlay.append("<td align=\"left\">")
                    sbPlay.append("<font size=\"5\">")
                    sbPlay.append("****")
                    sbPlay.append("</font></td></tr>")
                }
            }

            htmlContent =
                "<pre>" +
                        "<font size=\"4\">  " + "${getCurrentDate(date)}" + "</font><br>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_stub_equal)} $stubs${if (isRetry) {
                    R_string(R.string.print_retry_icon) + "\n"
                } else {
                    "\n"
                }}" + "</font>" +
                        "<font size=\"4\"><strong>" + "${R_string(R.string.print_unique_serie_equal)} $uniqueSerial" + "</strong></font><br>" +
                        "<font size=\"4\"><strong>" + "${R_string(R.string.print_check_equal)} $digitChecking" + "</strong></font><br>" +
                        "<font size=\"4\"><strong>" + "${R_string(R.string.print_seller_equal_upper)} $sellerName     M: ${getDeviceId()}" + "</strong></font><br>" +
                        "<font size=\"4\"><strong>" + "${R_string(R.string.print_raffle_date_equal)} $raffleDate " + "</strong></font>" +
                        "<font size=\"3\"><strong><p>" + "${stringLotteries}" + "</p></strong></font>" +
                        "<table style=\"width:100%\">" +
                        "<tr>" +
                        "<th  align=\"left\"><font size=\"2\" face=\"verdana\">No</font></th>" +
                        "<th><font size=\"2\" face=\"verdana\">Dir</th>" +
                        "<th><font size=\"2\" face=\"verdana\">Com</th>" +
                        "<th><font size=\"2\" face=\"verdana\">Pat</th>" +
                        "<th><font size=\"2\" face=\"verdana\">Uña</th>" +
                        "</tr>" +
                        sbPlay +
                        "</table>" +
                        "<font size=\"4\"face=\"verdana\">${R_string(R.string.print_bet_equal)}\$${formatValue(
                            totalValueBetting
                        )} Iva($iva):\$${formatValue(totalValueIva)}</font><br>" +
                        "<font size=\"4\"face=\"verdana\">${R_string(R.string.print_pay_equal)}\$${formatValue(
                            totalValuePaid
                        )} ${R_string(
                            R.string.print_over_equal
                        )}\$${formatValue(totalValueOverloaded)}</font><br>" +
                        "<font size=\"4\"face=\"verdana\">${R_string(R.string.print_terms_of_contract)}</font><br>" +
                        "<font size=\"4\"face=\"verdana\">${R_string(R.string.print_phone)}</font>" +
                        "<font size=\"4\" face=\"verdana\">  </font><br>" +
                        "<font size=\"4\" face=\"verdana\">  </font><br>" +
                        "<font size=\"4\" face=\"verdana\">  </font><br>" +
                        "</pre>"


        } catch (e: Exception) {
            Log.e("PRINT-FORMAT", "ERROR GENERANDO EL FORMATO DE IMPRESION")
            e.printStackTrace()
            callback(PrinterStatus.ERROR)
            return
        }

        try {

            if (openPrinter() != 0) {
                callback(PrinterStatus.BUSY)
                return
            }

            addLineBreak(3)

            printerDevice.printHTML(
                PagaTodoApplication.getAppContext(),
                htmlContent,
                object : PrinterHtmlListener {

                    override fun onGet(bitmap: Bitmap, i: Int) {
                        // not implemented
                    }

                    override fun onFinishPrinting(result: Int) {

                        when (result) {
                            PRINT_SUCCESS -> {

                                lpt_feed_dots(40)

                                if (lotteries.size > 4)
                                    lpt_feed_dots(20)
                                else
                                    lpt_feed_dots(50)

                                executeCallBack(PrinterStatus.OK, callback)
                            }
                            PRINT_ERROR -> executeCallBack(PrinterStatus.ERROR, callback)

                        }

                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            executeCallBack(PrinterStatus.ERROR, callback)
        }

    }

    override fun printPaymillionaire(
        totalValueBetting: Double,
        totalValuePaid: Double,
        totalValueIva: Double,
        raffleDate: String,
        lotteries: List<LotteryModel>,
        chances: List<ChanceModel>,
        iva: Int,
        sellerName: String,
        uniqueSerial: String,
        digitChecking: String,
        prizePlan: List<String>,
        selectedModeValue: ModeValueModel?,
        stubs: String,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {


        var htmlContent = ""

        try {
            val stringLotteries = getLotteriesWithPrintFormat(lotteries, 4)
            val arrayNumbersString = getPaymillionaireChancesGamesWithPrintFormat(chances)
            val digits = chances.get(0).number?.trim()?.length ?: 0

            val sbNumbers = StringBuilder()
            arrayNumbersString.forEach {
                sbNumbers.append("<font size=\"5\">")
                sbNumbers.append(it)
                sbNumbers.append("</font><br>")
            }
            val dateDraw = raffleDate.split("|")
            val stringData = "${dateDraw[0]} ${dateDraw[1]}"
            val date = DateUtil.convertStringToDateFormat(
                DateUtil.StringFormat.DDMMYY_HHMMSS_SPLIT_DASH,
                stringData
            )

            val sbPrizePlan = StringBuilder()
            var i = 0
            prizePlan.forEach {
                if (i > 0) {
                    sbPrizePlan.append("<font size=\"4\">")
                    sbPrizePlan.append(it)
                    sbPrizePlan.append("</font><br>")
                }
                i++
            }
            htmlContent =
                "<pre>" +
                        "<font size=\"4\" face=\"verdana\">  </font><br>" +
                        "<font size=\"4\" face=\"verdana\">  </font><br>" +
                        "<font size=\"4\" face=\"verdana\">  </font><br>" +
                        "<font size=\"4\">  " + "${getCurrentDate(date)}" + "</font><br>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_stub_equal)} $stubs${if (isRetry) {
                    R_string(R.string.print_retry_icon) + "\n"
                } else {
                    "\n"
                }}" + "</font>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_unique_serie_equal)} $uniqueSerial" + "</font><br>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_check_equal)} $digitChecking" + "</font><br>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_seller_equal_upper)} $sellerName     M: ${getDeviceId()}" + "</font><br>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_raffle_date_equal)} ${dateDraw[2]}" + "</font><br>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_paymillionaire)}" + " <" + stringLotteries + ">" + "</font><br>" +
                        "<font size=\"4\">" + "<${digits}C>     PLAN DE PREMIOS" + "</font><br>" +

                        "<table style=\"width:100%\">" +
                        "<td width=\"25%\" align=\"left\">" +

                        "<font size=\"4\">NUMERO</font><br>" +

                        "<font face=\"sans-serif\"><strong>" + sbNumbers.toString() + "</strong></font>" +

                        "</td>" +

                        "<td width=\"75%\" align=\"left\" valign=\"top\">" +

                        "<font face=\"verdana\">" + sbPrizePlan + "</font>" +

                        "</td>" +
                        "</table>" +

                        "<font size=\"4\" face=\"verdana\"> ${R_string(R.string.print_accumulated_equal)}\$${selectedModeValue?.accumulated} </font><br>" +
                        "<font size=\"4\" face=\"verdana\"> ${R_string(R.string.print_bet_equal)}\$${formatValue(
                            totalValueBetting
                        )} Iva($iva%):\$${formatValue(totalValueIva)} Nt:\$${formatValue(
                            totalValuePaid
                        )}</font><br>" +
                        "<font size=\"4\" face=\"verdana\"> ${R_string(R.string.print_terms_of_contract)}</font><br>" +
                        "<font size=\"4\" face=\"verdana\"> ${R_string(R.string.print_phone)}</font><br>" +
                        "<font size=\"4\" face=\"verdana\">  </font><br>" +
                        "<font size=\"4\" face=\"verdana\">  </font><br>" +
                        "<font size=\"4\" face=\"verdana\">  </font><br>" +
                        "</pre>"

        } catch (e: Exception) {
            Log.e("PRINT-FORMAT", "ERROR GENERANDO EL FORMATO DE IMPRESION")
            e.printStackTrace()
            callback(PrinterStatus.ERROR)
            return
        }

        try {

            if (openPrinter() != 0) {
                callback(PrinterStatus.BUSY)
                return
            }

            printerDevice.printHTML(
                PagaTodoApplication.getAppContext(),
                htmlContent,
                object : PrinterHtmlListener {

                    override fun onGet(bitmap: Bitmap, i: Int) {}

                    override fun onFinishPrinting(result: Int) {

                        when (result) {
                            PRINT_SUCCESS -> {
                                lpt_feed_dots(20)
                                executeCallBack(PrinterStatus.OK, callback)
                            }
                            PRINT_ERROR ->
                                executeCallBack(PrinterStatus.ERROR, callback)

                        }


                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            executeCallBack(PrinterStatus.ERROR, callback)
        }

    }

    /**
     * Método usado para obtener las apuestas realizadas en pagamillonario con el formato de impresión propuesto por el diseño
     */
    private fun getPaymillionaireChancesGamesWithPrintFormat(chances: List<ChanceModel>): List<String> {
        val response = arrayListOf<String>()
        chances.forEach {
            val number = it.number?.padStart(4, '*') ?: "****"
            response.add(number)
        }
        return response
    }

    override fun printMaxiChance(
        printModel: BasePrintModel,
        games: List<SuperchanceModel>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        var htmlContent = ""
        try {
            // Obtención de las loterías en un string con el formato de impresión
            val stringLotteries = getLotteriesWithPrintFormat(printModel.lotteries, 3)
            val dateDraw = printModel.raffleDate!!.split("|")
            val stringData = "${dateDraw[0]} ${dateDraw[1]}"
            val date = DateUtil.convertStringToDateFormat(
                DateUtil.StringFormat.DDMMYY_HHMMSS_SPLIT_DASH,
                stringData
            )

            val sbPlay = StringBuilder()
            var i = 0
            do {
                sbPlay.append("<tr valign=\"top\"><td valign=\"top\" width=\"25%\"><font face=\"verdana\" size=\"5\"><strong>")
                sbPlay.append(games[i].number?.padStart(4, '*'))
                sbPlay.append("</strong></font></td>")

                sbPlay.append("<td align=\"center\" valign=\"top\" width=\"18.5%\"><font face=\"verdana\" size=\"4\">")
                sbPlay.append(games[i].value?.trim().let { if (it.isNullOrEmpty()) "0" else it })
                sbPlay.append("</font></td>")
                sbPlay.append("<td align=\"center\" valign=\"top\" width=\"18.5%\"><font face=\"verdana\" size=\"4\">")
                sbPlay.append("0")
                sbPlay.append("</font></td>")
                sbPlay.append("<td align=\"center\" valign=\"top\" width=\"18.5%\"><font face=\"verdana\" size=\"4\">")
                sbPlay.append("0")
                sbPlay.append("</font></td>")
                sbPlay.append("<td align=\"center\" valign=\"top\" width=\"18.5%\"><font face=\"verdana\" size=\"4\">")
                sbPlay.append("0")
                sbPlay.append("</font></td></tr>")
                i++

            } while (i < games.count())
            if (games.count() < maxRowInChanceGame) {
                val rest = maxRowInChanceGame - games.count()
                for (index in 1..rest) {
                    sbPlay.append("<tr valign=\"top\">")
                    sbPlay.append("<td align=\"left\">")
                    sbPlay.append("<font size=\"5\">")
                    sbPlay.append("****")
                    sbPlay.append("</font></td></tr>")
                }
            }

            htmlContent =
                "<pre>" +
                        "<font size=\"4\">  " + "${getCurrentDate(date)}" + "</font><br>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_stub_equal)} ${printModel.stubs}${if (isRetry) {
                    R_string(R.string.print_retry_icon) + "\n"
                } else {
                    "\n"
                }}" + "</font>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_unique_serie_equal)} ${printModel.uniqueSerial}" + "</font><br>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_check_equal)} ${printModel.digitChecking}" + "</font><br>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_seller_equal_upper)} ${printModel.consultant}     M: ${getDeviceId()}" + "</font><br>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_raffle_date_equal)} ${dateDraw[2]}" + "</font><br>" +
                        "<font size=\"4\">${R_string(R.string.print_lottery_equal)} ${stringLotteries}</font><br>" +
                        "<font size=\"4\">         " + "${R_string(R.string.print_maxichance_name)}" + "</font><br>" +

                        "<table style=\"width:100%\">" +
                        "<tr>" +
                        "<th  align=\"left\"><font face=\"verdana\">No</font></th>" +
                        "<th><font face=\"verdana\">Dir</th>" +
                        "<th><font face=\"verdana\">Pat</th>" +
                        "<th><font face=\"verdana\">Com</th>" +
                        "<th><font face=\"verdana\">Uña</th>" +
                        "</tr>" +
                        sbPlay.toString() +
                        "</table>" +

                        "<font size=\"4\"face=\"verdana\">${R_string(R.string.print_bet_equal)}\$${formatValue(
                            printModel.totalValueBetting ?: 0.0
                        )} Iva(${printModel.iva}%):\$${formatValue(
                            printModel.totalValueIva ?: 0.0
                        )}</font><br>" +
                        "<font size=\"4\"face=\"verdana\">${R_string(R.string.print_pay_equal)}\$${formatValue(
                            printModel.totalValuePaid ?: 0.0
                        )} ${R_string(R.string.print_over_equal)}\$${formatValue(
                            printModel.totalValueOverloaded ?: 0.0
                        )}</font><br>" +
                        "<font size=\"4\"face=\"verdana\">${R_string(R.string.print_terms_of_contract)}</font><br>" +
                        "<font size=\"4\"face=\"verdana\">${R_string(R.string.print_phone)}</font></pre>"


        } catch (e: Exception) {
            Log.e("PRINT-FORMAT", "ERROR GENERANDO EL FORMATO DE IMPRESION")
            callback(PrinterStatus.ERROR)
            return
        }

        try {

            if (openPrinter() != 0) {
                callback(PrinterStatus.BUSY)
                return
            }

            lptFormat(1, 0, 0, 0, 0, 0, 0)
            lpt_feed_dots(70)

            printerDevice.printHTML(
                PagaTodoApplication.getAppContext(),
                htmlContent,
                object : PrinterHtmlListener {

                    override fun onGet(bitmap: Bitmap, i: Int) {}

                    override fun onFinishPrinting(result: Int) {

                        when (result) {
                            PRINT_SUCCESS -> {
                                if (printModel.lotteries?.size!! > 3) {
                                    lptFormat(1, 0, 0, 0, 0, 0, 0)
                                    lpt_feed_dots(40)
                                } else {

                                    lptFormat(1, 0, 0, 0, 0, 0, 0)
                                    lpt_feed_dots(90)
                                }
                                executeCallBack(PrinterStatus.OK, callback)
                            }
                            PRINT_ERROR ->
                                executeCallBack(PrinterStatus.ERROR, callback)

                        }


                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            executeCallBack(PrinterStatus.ERROR, callback)
        }


    }

    override fun printSuperChance(
        printModel: BasePrintModel,
        games: List<SuperchanceModel>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {


        var htmlContent = ""

        try {
            // Obtención de las loterías en un string con el formato de impresión
            val stringLotteries = getSuperChanceLotteriesWithPrintFormatQ22(printModel.lotteries)

            Log.e("stringLotteriessuper->", stringLotteries)

            val dateDraw = printModel.raffleDate!!.split("|")
            val stringData = "${dateDraw[0]} ${dateDraw[1]}"
            val date = DateUtil.convertStringToDateFormat(
                DateUtil.StringFormat.DDMMYY_HHMMSS_SPLIT_DASH,
                stringData
            )
            val sbPlay = StringBuilder()
            var i = 0
            do {
                sbPlay.append("<tr valign=\"top\"><td valign=\"top\" width=\"25%\"><font face=\"verdana\" size=\"5\"><strong>")
                sbPlay.append(games[i].number?.padStart(4, '*'))
                sbPlay.append("</strong></font></td>")

                sbPlay.append("<td align=\"center\" valign=\"top\" width=\"18.5%\"><font face=\"verdana\" size=\"4\">")
                sbPlay.append(games[i].value?.trim().let { if (it.isNullOrEmpty()) "0" else it })
                sbPlay.append("</font></td>")
                sbPlay.append("<td align=\"center\" valign=\"top\" width=\"18.5%\"><font face=\"verdana\" size=\"4\">")
                sbPlay.append("*****")
                sbPlay.append("</font></td>")
                sbPlay.append("<td align=\"center\" valign=\"top\" width=\"18.5%\"><font face=\"verdana\" size=\"4\">")
                sbPlay.append("*****")
                sbPlay.append("</font></td></tr>")
                i++

            } while (i < games.count())
            if (games.count() < (printModel.maxRows ?: 5)) {
                val rest = ((printModel.maxRows ?: 5) - games.size)
                for (index in 1..rest) {
                    sbPlay.append("<tr valign=\"top\">")
                    sbPlay.append("<td align=\"left\">")
                    sbPlay.append("<font size=\"5\">")
                    sbPlay.append("****")
                    sbPlay.append("</font></td></tr>")
                }
            }

            htmlContent =
                "<pre>" +
                        "<font size=\"4\">  " + "${getCurrentDate(date)}" + "</font><br>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_stub_equal)} ${printModel.stubs}${if (isRetry) {
                    R_string(R.string.print_retry_icon) + "\n"
                } else {
                    "\n"
                }}" + "</font>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_unique_serie_equal)} ${printModel.uniqueSerial}" + "</font><br>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_check_equal)} ${printModel.digitChecking}" + "</font><br>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_seller_equal_upper)} ${printModel.consultant}     M: ${getDeviceId()}" + "</font><br>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_raffle_date_equal)} ${dateDraw[2]}" + "</font><br>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_superchance_name)}</font>&lt; <b><font size=\"3\"> $stringLotteries &gt; </font></b><br>" +

                        "<table style=\"width:100%\">" +
                        "<tr>" +
                        "<th align=\"left\"><font size=\"2\" face=\"verdana\">No</font></th>" +
                        "<th><font size=\"2\" face=\"verdana\">Valor</th>" +
                        "</tr>" +
                        sbPlay +
                        "</table>" +

                        "<font size=\"4\"face=\"verdana\">${R_string(R.string.print_bet_equal)}\$${formatValue(
                            printModel.totalValueBetting ?: 0.0
                        )} Iva(${printModel.iva}%):\$${formatValue(
                            printModel.totalValueIva ?: 0.0
                        )}</font><br>" +
                        "<font size=\"4\"face=\"verdana\">${R_string(R.string.print_pay_equal)}\$${formatValue(
                            printModel.totalValuePaid ?: 0.0
                        )} ${R_string(
                            R.string.print_over_equal
                        )}\$${formatValue(printModel.totalValueOverloaded ?: 0.0)}</font><br>" +
                        "<font size=\"4\"face=\"verdana\">${R_string(R.string.print_terms_of_contract)}</font><br>" +
                        "<font size=\"4\"face=\"verdana\">${R_string(R.string.print_phone)}</font>" +
                        "<font size=\"4\" face=\"verdana\">  </font><br>" +
                        "<font size=\"4\" face=\"verdana\">  </font><br>" +
                        "<font size=\"4\" face=\"verdana\">  </font><br>" +
                        "</pre>"

        } catch (e: Exception) {
            Log.e("PRINT-FORMAT", "ERROR GENERANDO EL FORMATO DE IMPRESION")
            callback(PrinterStatus.ERROR)
            return
        }

        try {

            if (openPrinter() != 0) {
                callback(PrinterStatus.BUSY)
                return
            }

            addLineBreak(3)

            printerDevice.printHTML(
                PagaTodoApplication.getAppContext(),
                htmlContent,
                object : PrinterHtmlListener {

                    override fun onGet(bitmap: Bitmap, i: Int) {}

                    override fun onFinishPrinting(result: Int) {

                        when (result) {
                            PRINT_SUCCESS -> {
                                if(printModel.lotteries?.size!! > 3) {

                                    lptFormat(1, 0, 0, 0, 0, 0, 0)
                                    lpt_feed_dots(32)
                                } else {

                                    lptFormat(1, 0, 0, 0, 0, 0, 0)
                                    lpt_feed_dots(52)
                                }


                                executeCallBack(PrinterStatus.OK, callback)
                            }
                            PRINT_ERROR -> executeCallBack(PrinterStatus.ERROR, callback)

                        }

                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            executeCallBack(PrinterStatus.ERROR, callback)
        }
    }

}
