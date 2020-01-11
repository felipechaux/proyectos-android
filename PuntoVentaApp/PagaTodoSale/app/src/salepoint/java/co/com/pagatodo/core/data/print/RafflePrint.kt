package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.print.RafflePrintModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil
import co.com.pagatodo.core.util.print.POSPrinter
import co.com.pagatodo.core.util.print.tmu.EpsonPrinter

class RafflePrint: BasePrint(), IRafflePrint {

    override fun print(printModel: RafflePrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {

        val stringBuilder = StringBuilder()
        val posPrinter = POSPrinter()

        val municipalityCode =
            SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_municipality_code))
        val officeCode = SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_office_code))
        val codePointSale=SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_code_point_sale))


        stringBuilder.append("${getCurrentDate(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH)} ${getCurrentDate(DateUtil.StringFormat.HHMMSS)} M:${getDeviceId()}")
        stringBuilder.append("\n${getMaskedSellerCode()} ${printModel.stub?.replace("\\s".toRegex(), "")} ${municipalityCode} $officeCode $codePointSale")
        stringBuilder.append("\nSU: ${printModel.uniqueSerial} CS: ${printModel.digitChecked}")
        stringBuilder.append("\nNúmero: ${printModel.raffleNumber} Lotería: ${printModel.lotery}")
        stringBuilder.append("\nRifa: ${printModel.raffleName}")
        stringBuilder.append("\nValor Rifa: $${printModel.raffleValue} IVA INCLUIDO")
        stringBuilder.append("\nValor Premio: $${printModel.rafflePrize}")
        stringBuilder.append("\nDatos Sorteo: ${printModel.dateDraw} ${printModel.timeDraw}")
        stringBuilder.append("\n${completeDescription(printModel.raffleDescription?:"")}")
        stringBuilder.append("\n\n")

        val target = SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_jsa_printer))
        if (target.isNotEmpty()){
            EpsonPrinter().print(stringBuilder.toString(), callback)
        }else{
            posPrinter.print(stringBuilder.toString(), callback)
        }
    }


    private fun completeDescription(raffleDescription :String):String{

        if(raffleDescription.length <= 254){
            val fixLength =  (254 - raffleDescription.length)
            return String.format("%-${fixLength}s", raffleDescription);
        }else{
            return raffleDescription.substring(0,254)
        }



    }
}