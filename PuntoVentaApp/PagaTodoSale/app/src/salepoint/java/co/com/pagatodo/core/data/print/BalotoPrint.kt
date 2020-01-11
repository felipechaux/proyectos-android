package co.com.pagatodo.core.data.print

import android.util.Log
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.print.BalotoPrintModel
import co.com.pagatodo.core.util.RUtil
import com.pos.device.printer.Printer
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.util.print.AidlUtil
import co.com.pagatodo.core.util.print.POSPrinter
import co.com.pagatodo.core.util.print.tmu.EpsonPrinter
import java.util.*
import kotlin.concurrent.schedule

class BalotoPrint: BasePrint(), IBalotoPrint {

    val stringBuilder = StringBuilder()
    var headerBaloto=StringBuilder()
    var posPrinter = POSPrinter()
    var numberOfGames = 0
    var isEpson:Boolean=false

    override fun printBalotoSaleTicket(balotoPrintModel: BalotoPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        stringBuilder.clear()
        headerBaloto.clear()

        when(PrinterStatusInfo().getStatus()) {
            PrinterStatus.OK -> {
                val municipalityCode = SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_municipality_code))
                val officeCode = SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_office_code))
                val codePointSale=SharedPreferencesUtil.getPreference<String>(R_string(R.string.shared_key_code_point_sale))

                numberOfGames = 0



                headerBaloto.append(R_string(R.string.print_baloto_title)+"\n")
                stringBuilder.append(R_string(R.string.print_baloto_title)+"\n")
                splitDataHeaderTicket(balotoPrintModel.headerBaloto ?: "").forEachIndexed {index,baloto->
                    if(index==1){
                        headerBaloto.append("${baloto.toUpperCase()}\n")
                        stringBuilder.append("${baloto.toUpperCase()}\n")
                    }else{
                        headerBaloto.append("$baloto\n")
                        stringBuilder.append("$baloto\n")
                    }
                }


                stringBuilder.append("\n${balotoDateFormat(balotoPrintModel.transactionDate?:"")}  ${R_string(R.string.print_super_baloto)}")
                balotoPrintModel.numbers?.forEach { item ->
                    stringBuilder.append("\n${item.replace("       "," ").replace("  "," ")}")
                    numberOfGames++
                }

                val linePositionsToCenter = arrayOf(0,1,2,3,4,5,7+numberOfGames)


                stringBuilder.append("\n${R_string(R.string.print_baloto_value)} ${balotoPrintModel.valueBaloto}  ${R_string(R.string.print_baloto_revenge_value)} ${balotoPrintModel.valueRevenge}  ${R_string(R.string.text_label_iva_uppercase)} ${balotoPrintModel.iva}")
                stringBuilder.append("\n${R_string(R.string.print_baloto_without_iva_total)} ${balotoPrintModel.withoutIvaTotal}  ${R_string(R.string.text_label_total_value).toUpperCase()} ${balotoPrintModel.total}")
                stringBuilder.append("\n${balotoPrintModel.drawInfo}")
                stringBuilder.append("\n${R_string(R.string.print_baloto_num_pdv)}\t${balotoPrintModel.serialNumber}")
                stringBuilder.append("\n${R_string(R.string.print_baloto_seller)} ${balotoPrintModel.sellerCode}\t${R_string(R.string.print_baloto_term)} ${balotoPrintModel.terminal}")
                stringBuilder.append("\n${balotoPrintModel.sellerCode} ${balotoPrintModel.stubs} $municipalityCode $officeCode $codePointSale")

                when (balotoPrintModel.numbers?.size){
                    1 ->  stringBuilder.append("\n\n\n\n\n")
                    2 ->  stringBuilder.append("\n\n\n\n")
                    3 ->  stringBuilder.append("\n\n\n")
                    4 ->  stringBuilder.append("\n\n")
                    5 ->  stringBuilder.append("\n")
                }

                stringBuilder.append("${R_string(R.string.print_check_equal_pdv)} ${balotoPrintModel.checkNumber}")
                stringBuilder.append("\n${balotoPrintModel.transactionNumber}\n")
                stringBuilder.append("\n${balotoPrintModel.footerBaloto?.replace("\\n", "")}")

                val target = SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_jsa_printer))
                if (target.isNotEmpty()){
                    isEpson=true
                    EpsonPrinter().printBaloto(stringBuilder.toString().replace(headerBaloto.toString(),""), headerBaloto.toString(), callback)

                }else{

                    posPrinter.printWithAlignment(stringBuilder.toString(), linePositionsToCenter, callback).let {
                        callback(PrinterStatus.OK)
                    }
                }

            }
        }
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
        stringBuilder.clear()
        val municipalityCode = SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_municipality_code))
        val officeCode = SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_office_code))
                stringBuilder.append("\t    ${R_string(R.string.text_print_baloto_title)}")
                stringBuilder.append("\n\t${transactionDate}")
                stringBuilder.append("\n\n\t\t  ${R_string(R.string.text_print_baloto_subtitle)}")
                stringBuilder.append("\n      ${R_string(R.string.text_print_baloto_number)} ${numberTicket}")
                stringBuilder.append("\n\t     ${R_string(R.string.text_print_baloto_prize)} $${formatValue(valuePrize.toDouble())}")
                stringBuilder.append("\n\t     ${R_string(R.string.text_print_imp_ret)} $${formatValue(tax.toDouble())}")
                stringBuilder.append("\n\t     ${R_string(R.string.text_print_baloto_pay)} $${formatValue(valuePay.toDouble())}")
                stringBuilder.append("\n\t     ${R_string(R.string.text_print_baloto_terminal)} ${getDeviceId()}")
                stringBuilder.append("\n\t   ${R_string(R.string.text_print_baloto_pto_vta)} ${getDeviceId()}")
                stringBuilder.append("\n\n\t    ${R_string(R.string.text_print_baloto_number_authorizer)}")
                stringBuilder.append("\n\t  $authorizerNumber")
                stringBuilder.append("\n\n      $sellerCode $stub $municipalityCode $officeCode")
                stringBuilder.append("\n")

        print(stringBuilder.toString(), callback)
    }

    private fun splitDataHeaderTicket(text: String): List<String> {
        val list = mutableListOf<String>()
        text.split("\\n").forEach {
            list.add(it.trim())
        }
        return list
    }

    private fun print(data: String, callback: (printerStatus: PrinterStatus) -> Unit? = {}){
        val target = SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_jsa_printer))
        if (target.isNotEmpty()){
            EpsonPrinter().print(stringBuilder.toString(), callback)
        }else{
            val linePositionsToCenter = arrayOf(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16)
            posPrinter.printWithAlignment(stringBuilder.toString(), linePositionsToCenter, callback)
            //posPrinter.print(stringBuilder.toString(), callback)
        }
    }

    private fun  balotoDateFormat(s:String):String{

        return s.replace("Á","A")
            .replace("É","E")
            .replace("Í","I")
            .replace("Ó","O")
            .replace("Ú","U")
    }


}