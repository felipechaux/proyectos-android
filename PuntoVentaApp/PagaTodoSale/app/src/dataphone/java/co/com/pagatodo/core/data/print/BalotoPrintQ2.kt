package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.PagaTodoApplication.Companion.format
import co.com.pagatodo.core.PagaTodoApplication.Companion.printerDevice
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.print.BalotoPrintModel
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.formatValue
import com.cloudpos.printer.Format.*

class BalotoPrintQ2 : BasePrint(), IBalotoPrint {

    override fun printBalotoSaleTicket(
        printModel: BalotoPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        if(!(printModel.isExchange?:false)){

            if (openPrinter() != 0) {
                callback(PrinterStatus.BUSY)
                return
            }

        }




        try {

            addLineBreak(4)

            setFontStyle(format, 4, true, FORMAT_ALIGN_CENTER)
            printerDevice.printText(format, "${RUtil.R_string(R.string.print_baloto_title)}")

            lpt_feed_dots(6)
            splitDataHeaderTicket((printModel.headerBaloto?:"")).forEach{
                setFontStyle(format, 2, true, FORMAT_ALIGN_LEFT)
                printerDevice.printText(format, it)
            }
//            splitDataHeaderTicket(printModel.headerBaloto ?: "").forEachIndexed { index, baloto ->
//                if (index == 4) {
//                    setFontStyle(format, 3, true, FORMAT_ALIGN_LEFT)
//                    printerDevice.printText(format, baloto)
//                } else {
//                    setFontStyle(format, 2, true, FORMAT_ALIGN_LEFT)
//                    printerDevice.printText(format, baloto)
//                }
//            }
            setFontStyle(format,3,true, FORMAT_ALIGN_LEFT)
            printerDevice.printText(format, balotoDateFormat(printModel.transactionDate?:""))
            setFontStyle(format, 3, true, FORMAT_ALIGN_RIGHT)
            printerDevice.printText(format, "${RUtil.R_string(R.string.print_super_baloto)}")
            setFontStyle(format, 3, true, FORMAT_ALIGN_LEFT)
            var linesBreak = 7
            printModel.numbers?.forEach { item ->
                printerDevice.printText(format, item)
                linesBreak--
            }

            lpt_feed_dots(5)
            if(printModel.isExchange?:false){
                setFontStyle(format, 3, true, FORMAT_ALIGN_CENTER)
                printerDevice.printText(
                    format, RUtil.R_string(R.string.print_baloto_keep_playing))
            }

            setFontStyle(format, 3, true, FORMAT_ALIGN_LEFT)
            printerDevice.printText(
                format,
                "${RUtil.R_string(R.string.print_baloto_value)} ${printModel.valueBaloto} ${RUtil.R_string(
                    R.string.print_baloto_revenge_value
                )} ${printModel.valueRevenge}"
            )
            printerDevice.printText(
                format,
                "${RUtil.R_string(R.string.text_label_iva_uppercase)} ${printModel.iva}"
            )
            printerDevice.printText(
                format,
                "${RUtil.R_string(R.string.print_baloto_without_iva_total)} ${printModel.withoutIvaTotal}"
            )
            printerDevice.printText(
                format,
                "${RUtil.R_string(R.string.text_label_total_value).toUpperCase()} ${printModel.total}"
            )


            setFontStyle(format, 3, false, FORMAT_ALIGN_LEFT)

            linesBreak = 11
            printModel.drawInfo =
                printModel.drawInfo?.substring(0, printModel.drawInfo!!.length?.minus(2))
            var linesToPrint = printModel.drawInfo!!.length / 32
            if (printModel.drawInfo!!.length / 32 == 0 && printModel.drawInfo!!.length.rem(32) != 0)
                linesBreak -= 1
            else if (printModel.drawInfo!!.length / 32 != 0 && printModel.drawInfo!!.length.rem(32) != 0)
                linesBreak -= linesToPrint + 1
            else
                linesBreak -= linesToPrint
            printerDevice.printText(format, printModel.drawInfo)
            setFontStyle(format, 2, true, FORMAT_ALIGN_LEFT)

//            addLineBreak(linesBreak)
            linesBreak = 14
            var emptySpace = printModel.numbers!!.size
            if (emptySpace <= 3){
                addLineBreak(linesBreak-emptySpace-1)
            }
            else {
                addLineBreak(linesBreak-emptySpace-3)
            }
//            addLineBreak(linesBreak-emptySpace)
            printBalotoTransactionTicket(printModel, callback)

        } catch (e: Exception) {
            e.printStackTrace()
            callback(PrinterStatus.ERROR)
            finalizePrinter()
        }
    }


    fun printBalotoTransactionTicket(
        printModel: BalotoPrintModel,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        if(printModel.isExchange?:false){
            addLineBreak(5)
        }else{
            addLineBreak(7)
        }


        printerDevice.printText(
            format,
            "${RUtil.R_string(R.string.print_baloto_num)} ${printModel.serialNumber}"
        )
        printerDevice.printText(
            format,
            "${RUtil.R_string(R.string.print_baloto_seller)} ${printModel.sellerCode}"
        )
        printerDevice.printText(
            format,
            "${RUtil.R_string(R.string.print_baloto_term)} ${getDeviceId()}"
        )
        addLineBreak(2)
        printerDevice.printText(
            format,
            "${RUtil.R_string(R.string.print_seller_equal_upper)} ${printModel.sellerCode}"
        )
        printerDevice.printText(
            format,
            "${RUtil.R_string(R.string.print_stub_equal)} ${printModel.stubs}"
        )

        printerDevice.printText(
            format,
            "${RUtil.R_string(R.string.print_check_equal)} ${printModel.checkNumber}"
        )
        printerDevice.printText(format, "        ${printModel.transactionNumber}")
        setFontStyle(format, 2, true, FORMAT_ALIGN_LEFT)
        printerDevice.printText(format, printModel.footerBaloto?.replace("\\n", ""))


        if(printModel.isExchange?:false){
            addLineBreak(10)
        }else{
            addLineBreak(11)
            finalizePrinter()
        }
        callback(PrinterStatus.OK)


    }

    override fun printBalotoQueryTicket(
        transactionDate: String,
        numberTicket: String,
        valuePrize: String,
        tax: String,
        valuePay: String,
        terminalCode: String,
        salePointCode: String,
        authorizerNumber: String,
        stub: String,
        sellerCode: String,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {


        openPrinter()

            try {
                addLineBreak(5)
                setFontStyle(format, 3, false, FORMAT_ALIGN_CENTER)
                printerDevice.printText(format, "${RUtil.R_string(R.string.text_print_baloto_title)}")
                printerDevice.printText(format, " ")

                printerDevice.printText(format, "${transactionDate}")
                printerDevice.printText(format, " ")
                printerDevice.printText(format, "${RUtil.R_string(R.string.text_print_baloto_subtitle)}")
                printerDevice.printText(format,
                    "${RUtil.R_string(R.string.text_print_baloto_number)} ${numberTicket}"
                )
                printerDevice.printText(format,
                    "${RUtil.R_string(R.string.text_print_baloto_prize)} $${formatValue(valuePrize.toDouble())}")
                printerDevice.printText(format,
                    "${RUtil.R_string(R.string.text_print_imp_ret)} $${formatValue(tax.toDouble())}")
                printerDevice.printText(format,
                    "${RUtil.R_string(R.string.text_print_baloto_pay)} $${formatValue(valuePay.toDouble())}")
                printerDevice.printText(format,
                    "${RUtil.R_string(R.string.text_print_baloto_terminal)} ${getDeviceId()}"
                )
                printerDevice.printText(format,
                    "${RUtil.R_string(R.string.text_print_baloto_pto_vta)} ${getDeviceId()}"
                )
                printerDevice.printText(format, " ")
                printerDevice.printText(format,
                    RUtil.R_string(R.string.text_print_baloto_number_authorizer)
                )
                printerDevice.printText(format, authorizerNumber)
                printerDevice.printText(format, " ")
                printerDevice.printText(format,
                    "${RUtil.R_string(R.string.text_print_baloto_stub)} ${stub}"
                )
                printerDevice.printText(format,
                    "${RUtil.R_string(R.string.text_print_baloto_seller_code)} ${sellerCode}"
                )
                addLineBreak(9)
                callback(PrinterStatus.OK)
                finalizePrinter()

            } catch (e: Exception) {
                e.printStackTrace()
                callback(PrinterStatus.ERROR)
                finalizePrinter()
            }
    }

    private fun splitDataHeaderTicket(text: String): List<String> {
        val list = mutableListOf<String>()
        text.split("\\n").forEach {
            list.add(it.trim())
        }
        return list
    }

    private fun  balotoDateFormat(s:String):String{

        return s.replace("Á","A")
            .replace("É","E")
            .replace("Í","I")
            .replace("Ó","O")
            .replace("Ú","U")
    }
}