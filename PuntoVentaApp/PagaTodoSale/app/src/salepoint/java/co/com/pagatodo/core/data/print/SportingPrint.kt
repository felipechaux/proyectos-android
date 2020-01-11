package co.com.pagatodo.core.data.print

import android.graphics.Paint
import co.com.pagatodo.core.PagaTodoApplication.Companion.format
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.SportingBetModel
import co.com.pagatodo.core.data.model.print.SportingPrintModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.formatValue
import co.com.pagatodo.core.util.print.POSPrinter
import com.cloudpos.printer.Format.FORMAT_ALIGN_LEFT

class SportingPrint: BasePrint(), ISportingPrint {
    override fun print(printModel: SportingPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        val stringBuilder = StringBuilder()
        val posPrinter = POSPrinter()

        printModel.header?.split("\\\\n")?.forEachIndexed { index, item ->
       
                stringBuilder.append(item)

        }
        if (printModel.productCode == R_string(R.string.code_sporting_league_product).toInt()){
            val cant2 = printModel.teams!!.size
            stringBuilder.append("\nF.Venta:${getCurrentDate(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH)} " +
                    "${getCurrentDate(DateUtil.StringFormat.HHMMSS)}" + stringBuilder.append("\nF.Grilla: ${printModel.grid}" +
                    " PDV:${getPreference<String>(R_string(R.string.shared_key_office_code))}")

                    +"${R_string(R.string.print_seller)}${getMaskedSellerCode()}" + stringBuilder.append("CANT:$cant2\n") +
            stringBuilder.append("\nId: "+getDeviceId()+"\tTiq: ${printModel.stub?.replace("\\s".toRegex(), "")}\n"))

            val rowString = StringBuilder("")

            printModel.teams?.forEachIndexed { index, item ->
                val meetingResult = getResultIndicator(item)
                val counter = "${index + 1}"
                if ( (index + 1) % 2 == 0) {
                    rowString.append("  |  ${counter.padStart(2, ' ')}. ${item.local} Vs ${item.visitor} : $meetingResult\n")
                }
                else {
                    rowString.append("${counter.padStart(2, ' ')}. ${item.local} Vs ${item.visitor} : $meetingResult")
                }
            }
            stringBuilder.append(rowString.toString())

        }
        else {
            stringBuilder.append("\nF.Venta: ${getCurrentDate(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH)} " +
                    "${getCurrentDate(DateUtil.StringFormat.HHMMSS)}" +
                    " PDV:${getPreference<String>(R_string(R.string.shared_key_office_code))}")
            stringBuilder.append("\nF.Grilla:${printModel.grid}"
                    +"\t${R_string(R.string.print_seller)}${getMaskedSellerCode()}")
            stringBuilder.append("\nId: "+getDeviceId()+"\tTiq: ${printModel.stub?.replace("\\s".toRegex(), "")}\n")

            printModel.teams?.forEachIndexed { index, team ->
                stringBuilder.append("${team.code!!.toInt()}.  ${team.local}: (${team.localMarker})   Vs   ${team.visitor}: (${team.visitorMarker})\n")
                val cant = printModel.teams!!.size
                stringBuilder.append("CANT:$cant\n")
            }
        }

//        stringBuilder.append("\nF.Venta: ${getCurrentDate(DateUtil.StringFormat.DDMMYY_SPLIT_BACKSLASH)} " +
//                "${getCurrentDate(DateUtil.StringFormat.HHMMSS)}" +
//                " PDV:${getPreference<String>(R_string(R.string.shared_key_office_code))}")
//        stringBuilder.append("\nF.Grilla: ${printModel.grid}"
//                +"\t${R_string(R.string.print_seller)}${getMaskedSellerCode()}")
//        stringBuilder.append("\nId: "+getDeviceId()+"\tTiq: ${printModel.stub?.replace("\\s".toRegex(), "")}\n")

//        if (printModel.productCode == R_string(R.string.code_sporting_league_product).toInt()){
//            val rowString = StringBuilder("")
//
//            printModel.teams?.forEachIndexed { index, item ->
//                val meetingResult = getResultIndicator(item)
//                val counter = "${index + 1}"
//                if ( (index + 1) % 2 == 0) {
//                    rowString.append("  |  ${counter.padStart(2, ' ')}. ${item.local} Vs ${item.visitor} : $meetingResult\n")
//                }
//                else {
//                    rowString.append("${counter.padStart(2, ' ')}. ${item.local} Vs ${item.visitor} : $meetingResult")
//                }
//            }
//            stringBuilder.append(rowString.toString())
//
//        } else {
//            printModel.teams?.forEachIndexed { index, team ->
//                stringBuilder.append("${team.code!!.toInt()}.  ${team.local}: (${team.localMarker})   Vs   ${team.visitor}: (${team.visitorMarker})\n")
//                val cant = printModel.teams!!.size
//                stringBuilder.append("CANT:$cant\n")
//            }
//        }




        stringBuilder.append("Total: $${formatValue((printModel.totalValue ?: "0").toDouble())} " +
                "Neto: $${formatValue((printModel.value ?: "0").toDouble())} " +
                "IVA: \$${formatValue(
            (printModel.iva ?: "0").toDouble()
        )}")

        val itemsFooter = printModel.footer?.split("\\\\n") ?: arrayListOf()
        stringBuilder.append("\n${itemsFooter[0]} ${itemsFooter[1]}")
        stringBuilder.append("\n${itemsFooter[2]}")

        posPrinter.print(stringBuilder.toString(), callback)
    }

    private fun getResultIndicator(meeting: SportingBetModel): String {
        var response = "E"
        if (meeting.isLocalResult == true)
            response = "L"
        else if (meeting.isVisitorResult == true)
            response = "V"
        return response
    }
}