package co.com.pagatodo.core.data.print

import android.graphics.Bitmap
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.GirorCheckRequestsDTO
import co.com.pagatodo.core.data.dto.response.ResponseGiroLoginDTO
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil
import com.cloudpos.DeviceException
import com.google.gson.Gson
import com.pos.device.printer.PrintCanvas
import com.wizarpos.htmllibrary.PrinterHtmlListener
import java.util.*


class GiroPrintQ2 : BasePrint(), IGiroPrint {
    override fun printPaymentGiro(
        data: List<String>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        val sbPrintData = StringBuilder()

        sbPrintData.append("<table cellpadding=\"0\" style = \"width:100%\">")

        data.forEach {
            sbPrintData.append("<tr valign=\"top\">")
            sbPrintData.append("<td align=\"${validateCenter(it)}\" valign=\"top\" width=\"70.0%\">")
            sbPrintData.append(getLine(it))
            sbPrintData.append("</td>")
            sbPrintData.append("</tr>")
        }

        sbPrintData.append("</table")
        executeHtmlPrintQ2(sbPrintData.toString(), 13, 0,0, callback)


    }

    override fun printCaptureGiro(
        data: List<String>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        printPaymentGiro(data, isRetry, callback)
    }

    override fun printCheckRequest(
        girorCheckRequestsDTO: GirorCheckRequestsDTO,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        val sesionGiroLocal = getLocalGiroLogin()
        val stringBuilder = StringBuilder()
        stringBuilder.clear()

        stringBuilder.append(" </b>")
        stringBuilder.append("<p align=\"center\">${sesionGiroLocal.company?.companyName}</p>")
        stringBuilder.append("<p align=\"center\">${sesionGiroLocal.company?.companyNit}</p>")
        stringBuilder.append("<p align=\"center\">${sesionGiroLocal.company?.companyUrl}</p>")

        stringBuilder.append("<p align=\"center\">${sesionGiroLocal.company?.companyPhone}</p>")
        stringBuilder.append("<p align=\"center\">${girorCheckRequestsDTO.requestName}</p>")
        stringBuilder.append(
            "<p>PIN: ${girorCheckRequestsDTO.reference} (${SharedPreferencesUtil.getPreference<String>(
                RUtil.R_string(R.string.shared_key_user_id)
            )})</p>"
        )
        stringBuilder.append("<p>Tercero origen:${girorCheckRequestsDTO.beneficiaryName}</p>")
        stringBuilder.append("<p>FIRMA</p>")
        stringBuilder.append("</b>")
        stringBuilder.append("</b>")
        stringBuilder.append("</b>")
        stringBuilder.append("<p> _________________________________</p>")
        stringBuilder.append(
            "<p> IMPRESION      :${DateUtil.convertDateToStringFormat(
                DateUtil.StringFormat.DDMMYY_HHMMA_SPLIT_BACKSLASH,
                Date()
            )}</p>"
        )

        stringBuilder.append("<p>COMPROBANTE Nro:${girorCheckRequestsDTO.idRequest}</p>")

        executeHtmlPrintQ2(stringBuilder.toString(), 13, 0,0, callback)

    }

    override fun printCloseBox(
        data: List<String>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        val sbPrintData = StringBuilder()

        data.forEach {
            sbPrintData.append("<pre>${it}</pre>")
        }

        executeHtmlPrintQ2(sbPrintData.toString(), 13, 0,0, callback)

    }

    private fun getLine(line: String): String {
        if (line != "") {

            if(!line.contains("|")){

                return line

            }else{

                var line = line.split(RUtil.R_string(R.string.giro_print_format))[1]

                if (line == " ") {
                    line = "</br>"
                }

                return line
            }

        } else {
            return "</br>"
        }


    }

    private fun validateCenter(line: String): String {

        return if (line.split(RUtil.R_string(R.string.giro_print_format))[0] == RUtil.R_string(R.string.giro_print_aling_center)) "center" else "left"
    }


    private fun getLocalGiroLogin(): ResponseGiroLoginDTO {
        val json: String =
            SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_giro_login))
        return Gson().fromJson(json, ResponseGiroLoginDTO::class.java);
    }


}