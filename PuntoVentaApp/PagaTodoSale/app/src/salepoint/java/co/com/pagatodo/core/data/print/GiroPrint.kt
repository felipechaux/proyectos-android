package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.GirorCheckRequestsDTO
import co.com.pagatodo.core.data.dto.response.ResponseGiroLoginDTO
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil
import co.com.pagatodo.core.util.print.POSPrinter
import com.google.gson.Gson
import java.util.*

class GiroPrint : IGiroPrint {


    override fun printPaymentGiro(
        data: List<String>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        val stringBuilderPaymentGiro = StringBuilder()
        val posPrinter = POSPrinter()
        stringBuilderPaymentGiro.clear()

        var linePositionsToCenter = arrayOf(data.size)
        data.forEachIndexed { index, string ->

            if (validateCenter(string)) {
                linePositionsToCenter += index
            }

            stringBuilderPaymentGiro.append("${getLine(string)}\n")
        }

        posPrinter.printWithAlignment(
            stringBuilderPaymentGiro.toString(),
            linePositionsToCenter,
            callback
        )

    }

    override fun printCaptureGiro(
        data: List<String>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        val stringBuilderCaptureGiro = StringBuilder()
        val posPrinter = POSPrinter()
        stringBuilderCaptureGiro.clear()

        var linePositionsToCenter = arrayOf(data.size)
        data.forEachIndexed { index, string ->

            if (validateCenter(string)) {
                linePositionsToCenter += index
            }

            stringBuilderCaptureGiro.append("${getLine(string)}\n")
        }

        posPrinter.printWithAlignment(
            stringBuilderCaptureGiro.toString(),
            linePositionsToCenter,
            callback
        )


    }

    override fun printCheckRequest(
        girorCheckRequestsDTO: GirorCheckRequestsDTO,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        // implementar impresion de consulta de solicitudes

        val sesionGiroLocal = getLocalGiroLogin()
        val stringBuilder = StringBuilder()
        val posPrinter = POSPrinter()
        stringBuilder.clear()

        stringBuilder.append(" \n")
        stringBuilder.append("${sesionGiroLocal.company?.companyName}\n")
        stringBuilder.append("${sesionGiroLocal.company?.companyNit}\n")
        stringBuilder.append("${sesionGiroLocal.company?.companyUrl}\n")

        stringBuilder.append("${sesionGiroLocal.company?.companyPhone}")
        stringBuilder.append(" \n")
        stringBuilder.append(" ${girorCheckRequestsDTO.requestName}\n")
        stringBuilder.append(
            "PIN: ${girorCheckRequestsDTO.reference} (${SharedPreferencesUtil.getPreference<String>(
                RUtil.R_string(R.string.shared_key_user_id)
            )})\n"
        )
        stringBuilder.append("Tercero origen:${girorCheckRequestsDTO.beneficiaryName}\n")
        stringBuilder.append("FIRMA\n")
        stringBuilder.append(" \n")
        stringBuilder.append(" \n")
        stringBuilder.append(" \n")
        stringBuilder.append("_________________________________\n")
        stringBuilder.append(
            "IMPRESION      :${DateUtil.convertDateToStringFormat(
                DateUtil.StringFormat.DDMMYY_HHMMA_SPLIT_BACKSLASH,
                Date()
            )}\n"
        )

        stringBuilder.append("COMPROBANTE Nro:${girorCheckRequestsDTO.idRequest}\n")

        val linePositionsToCenter = arrayOf(1, 2, 3, 4, 5)

        posPrinter.printWithAlignment(stringBuilder.toString(), linePositionsToCenter, callback)


    }

    override fun printCloseBox(
        data: List<String>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        val stringBuilderCaptureGiro = StringBuilder()
        val posPrinter = POSPrinter()
        stringBuilderCaptureGiro.clear()

        data.forEachIndexed { index, string ->

            stringBuilderCaptureGiro.append("$string\n")
        }

        posPrinter.print(stringBuilderCaptureGiro.toString(), callback)

    }

    private fun validateCenter(line: String): Boolean {
        if (line.isNotEmpty())
            return line.split(RUtil.R_string(R.string.giro_print_format))[0] == RUtil.R_string(R.string.giro_print_aling_center)
        else
            return false
    }

    private fun getLine(line: String): String {

        if (line.isNotEmpty()) {

            if (!line.contains("|")) {

                return line
            } else {
                return line.split(RUtil.R_string(R.string.giro_print_format))[1]
            }

        } else
            return ""
        
    }

    private fun getLocalGiroLogin(): ResponseGiroLoginDTO {
        val json: String =
            SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_giro_login))
        return Gson().fromJson(json, ResponseGiroLoginDTO::class.java);
    }


}