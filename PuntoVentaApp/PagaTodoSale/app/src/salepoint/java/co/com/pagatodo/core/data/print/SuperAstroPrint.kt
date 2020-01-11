package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.print.SuperAstroPrintModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil
import co.com.pagatodo.core.util.print.POSPrinter
import co.com.pagatodo.core.util.print.tmu.EpsonPrinter

class SuperAstroPrint: BasePrint(), ISuperAstroPrint {

    override fun print(printModel: SuperAstroPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {

        val stringBuilder = StringBuilder()
        val posPrinter = POSPrinter()

        // validar solo 3 lineas de encabezado
        val arrayHeader = printModel.textHeader?.replace("\\\\n","\n")?.replace("\\n","\n")


        stringBuilder.append(arrayHeader)
        
        val municipalityCode = SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_municipality_code))
        val officeCode = SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_office_code))
        val codePointSale=SharedPreferencesUtil.getPreference<String>(R_string(R.string.shared_key_code_point_sale))
        stringBuilder.append("\n${printModel.draw}-${R_string(R.string.print_draw_date_equal_pdv)}${DateUtil.convertDateToStringFormat(DateUtil.StringFormat.DDMMYY,DateUtil.getCurrentDate())}")
        stringBuilder.append("\n${getMaskedSellerCode()} ${printModel.stub?.replace("\\s".toRegex(),"")}  ${municipalityCode} ${officeCode} $codePointSale ${if(isRetry){ R_string(R.string.print_retry_icon)}else{""}}")
        stringBuilder.append("\n${getCurrentDate(DateUtil.StringFormat.DDMMYYYY_SPLIT_DASH)}" +
                "${getCurrentDate(DateUtil.StringFormat.HHMMSS)}" +
                "CV:${printModel.digitChecking}M:${getDeviceId().substring(0,5)}")
        var index = 0
        val planPrizeList = printModel.ticketPrizePlan?.split(";")
        printModel.superastroList?.forEach{
            val select = it.zodiacSelected?.split("-")?.get(1) ?: " "
            stringBuilder.append("\n${it.number} $${it.value?.toInt() ?: 0} ${select.padEnd(7)}" +
                    " ${if(index < planPrizeList?.size?: 0){planPrizeList?.get(index)} else {""}}")
            index++
        }

        while (index < planPrizeList?.size?: 0){
            stringBuilder.append("\n\t\t   ${planPrizeList?.get(index)}")
            index++
        }

        stringBuilder.append("\n${R_string(R.string.print_pay_equal_pdv)}${printModel.totalValuePaid?.toInt()} " +
                "${R_string(R.string.text_iva_equal).toLowerCase().capitalize()}${printModel.totalValueIva?.toInt()} " +
                "${R_string(R.string.print_bets_equal_pdv)}${printModel.totalValueBetting?.toInt()}")
        stringBuilder.append("\n${printModel.ticketFooter}")

        val target = SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_jsa_printer))
        if (target.isNotEmpty()){
            EpsonPrinter().print(stringBuilder.toString(), callback)
        }else{
            posPrinter.print(stringBuilder.toString(), callback)
        }
    }

}

