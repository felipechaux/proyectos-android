package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.util.print.POSPrinter
import co.com.pagatodo.core.util.print.tmu.EpsonPrinter
import java.lang.Exception

class RechargePrint: BasePrint(), IRechargePrint {

    val stringBuilder = StringBuilder()
    var posPrinter = POSPrinter()

    override fun printRecharge(
        stub: String,
        billNumber: String,
        transactionDate: String,
        transactionHour: String,
        number: String,
        value: String,
        billPrefix: String,
        billMessage: String,
        isRetry: Boolean,
        headerGelsa: String,
        decriptionPackage:String,
        operationName :String,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        stringBuilder.clear()

        val sellerCode = getPreference<String>(R_string(R.string.shared_key_seller_code))
        val municipalityCode = getPreference<String>(R_string(R.string.shared_key_municipality_code))
        val officeCode = getPreference<String>(R_string(R.string.shared_key_office_code))
        val imei = getDeviceId()
        val codePointSale=getPreference<String>(R_string(R.string.shared_key_code_point_sale))
        
        var indexItem = 0

        val arrayCenterItems = mutableListOf<Int>()
        arrayCenterItems.add(indexItem)

        headerGelsa.split("\\n").forEach {
            stringBuilder.append("$it\n")
            indexItem += 1
            arrayCenterItems.add(indexItem)
        }

        if(decriptionPackage.isNotEmpty()){
            stringBuilder.append("\n${getCurrentDate(DateUtil.StringFormat.DMMMYYHOUR_SPLIT_WHITE_SPACE)}")
            stringBuilder.append("\nAsesora: $sellerCode")
            stringBuilder.append("\nCHEQUEO: 21e6269357083b1a3")
            stringBuilder.append("\n\nPAQUETE")
            stringBuilder.append("\nNombre: $decriptionPackage")
            stringBuilder.append("\nDesc: $decriptionPackage")
            stringBuilder.append("\nOperador: $operationName")
            stringBuilder.append("\nNúmero: $number")
            stringBuilder.append("\nValor: ${formatValue(value.toDouble())}")
        }
        else{
            stringBuilder.append("\n$sellerCode $municipalityCode $officeCode $codePointSale ${if(isRetry){R_string(R.string.print_retry_icon)} else{""}}")
            stringBuilder.append("\nNo Factura: $billPrefix-$billNumber")
            stringBuilder.append("\nFecha: ${getCurrentDate(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH)}")
            stringBuilder.append("\nHora: ${getCurrentDate(DateUtil.StringFormat.HHMMSS)}")
            stringBuilder.append("\nLínea Recargada: $number")
            stringBuilder.append("\nValor Recarga: $${formatValue(value.toDouble())}")
            stringBuilder.append("\n-$billPrefix\n")
            indexItem += 7

            if (billMessage.isNotEmpty()) {
                billMessage.split("\\n").forEach {
                    indexItem += 1
                    stringBuilder.append(it)
                    arrayCenterItems.add(indexItem)
                }

            }

        }

        try {


            posPrinter.printWithAlignment(stringBuilder.toString(),arrayCenterItems.toTypedArray(), callback)
            callback(PrinterStatus.OK)

        }
        catch (e: Exception) {
            callback(PrinterStatus.ERROR)
        }
    }

    override fun printBetplay(
        documentId: String,
        value: Int,
        sellerCode: String,
        digitChecking: String,
        stubs: String,
        isRetry: Boolean,
        typePrintText: String,
        isReprint: Boolean,
        isCollect: Boolean,
        callback: (printerStatus: PrinterStatus)
        -> Unit?
    ) {
        stringBuilder.clear()

        val officeCode = getPreference<String>(R_string(R.string.shared_key_office_code))
        val codePointSale=getPreference<String>(R_string(R.string.shared_key_code_point_sale))

        stringBuilder.append("$sellerCode\t$stubs $officeCode $codePointSale ${if(isRetry){R_string(R.string.print_retry_icon)} else{""}}")

        if (isCollect){
            val currentDate = getCurrentDate(DateUtil.StringFormat.EEE_DD_MM_YYYY).toLowerCase()
            stringBuilder.append("\n${currentDate} ${getCurrentDate(DateUtil.StringFormat.HHMMSS)}")
        }else{
            stringBuilder.append("\n${getCurrentDate(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH)} ${getCurrentDate(DateUtil.StringFormat.HHMMSS)}")
        }

        stringBuilder.append("\n\n\t     $typePrintText")
        stringBuilder.append("\n\n${R_string(R.string.print_document_text)}: ${documentId}")

        stringBuilder.append("\n${R_string(R.string.print_recharge_value)}: \$${formatValue(value.toDouble())}")

        stringBuilder.append("\n\n\t     ${digitChecking}")

        if(isReprint)stringBuilder.append("\n\n${R_string(R.string.print_reprint_text)}")
        else stringBuilder.append("\n\n${R_string(R.string.print_original_text)}")
        stringBuilder.append("\n\n\n\n\n")
        val linePositionsToCenter = arrayOf(3,8)
        //posPrinter.printWithAlignment(stringBuilder.toString(), linePositionsToCenter, callback)
        print(stringBuilder.toString(), linePositionsToCenter, callback)

    }


    private fun print(data: String, arr:Array<Int>,callback: (printerStatus: PrinterStatus) -> Unit? = {}){
        val target = getPreference<String>(R_string(R.string.shared_key_jsa_printer))
        if (target.isNotEmpty()){
            EpsonPrinter().print(data, callback)
        }else{
            posPrinter.printWithAlignment(data,arr, callback)
        }
    }

}