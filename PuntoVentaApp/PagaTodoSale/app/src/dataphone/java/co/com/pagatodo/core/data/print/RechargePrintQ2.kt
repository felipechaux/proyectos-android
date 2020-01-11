package co.com.pagatodo.core.data.print

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.DeviceUtil.Companion.getSerialNumber
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.formatValue
import com.cloudpos.DeviceException
import com.wizarpos.htmllibrary.PrinterHtmlListener

class RechargePrintQ2 : BasePrint(), IRechargePrint {

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
        decriptionPackage: String,
        operationName: String,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        val sellerCode = getPreference<String>(R_string(R.string.shared_key_seller_code))
        val municipalityCode =
            getPreference<String>(R_string(R.string.shared_key_municipality_code))
        val officeCode = getPreference<String>(R_string(R.string.shared_key_office_code))
        val imei = getDeviceId()
        val serialNumber = getSerialNumber()

        var htmlContent = ""

        try {

            val stringFooter = StringBuilder()

            if (billMessage.isNotEmpty()) {

                stringFooter.append("<center>")
                billMessage.split("\\n").forEach {
                    stringFooter.append("<font size=\"4\">$it</font><br>")
                }
                stringFooter.append("</center>")


            } else {
                stringFooter.append("<center><font size=\"4\"><br></font><font size=\"4\"><br></font><font size=\"4\"><br></font></center>")
            }

            if (decriptionPackage.isNotEmpty()) {
                htmlContent =
                    "<pre><br></br><br></br>" +
                            "<font size=\"4\">" +
                            "<center>" +
                            "$transactionDate  $transactionHour" +
                            "</center><br>" +
                            "Asesora: $sellerCode M: $serialNumber<br>" +
                            "Colilla: $stub<br>" +
                            "CHEQUEO: 21e6269357083b1a3<br><br>" +
                            "PAQUETE<br>" +
                            "Nombre: $decriptionPackage<br>" +
                            "Desc: $decriptionPackage<br>" +
                            "Operador   : $operationName<br>" +
                            "Numero     : $number<br>" +
                            "Valor      : $value<br><br>" +
                            "<center>" +
                            "Total: $${formatValue(value.toDouble())}" +
                            "</center><br>" +
                            "</font>" +
                            "</pre>" + "$billPrefix"


            } else {
                htmlContent =
                    "<pre><br></br><br></br>" +
                            "<font size=\"4\">" + "$sellerCode $municipalityCode $officeCode $imei" + "</font><br>" +
                            "<font size=\"4\">" + "Colilla: $stub" + "</font><br>" +
                            "<font size=\"4\">" + "No Factura: $billPrefix-$billNumber" + "</font><br>" +
                            "<font size=\"4\">" + "Fecha: $transactionDate" + "</font><br>" +
                            "<font size=\"4\">" + "Hora: $transactionHour" + "</font><br>" +
                            "<font size=\"4\">" + "Línea Recargada: $number" + "</font><br>" +
                            "<font size=\"4\">" + "Valor Recarga: $${formatValue(value.toDouble())}" + "</font><br><br>" +
                            "<font size=\"4\">" + "-$billPrefix" + "</font><br>" +
                            "</pre>" +
                            stringFooter.toString()

            }


        } catch (e: Exception) {
            e.printStackTrace()
            callback(PrinterStatus.ERROR)
            return
        }

        executeHtmlPrintQ2(generatePrintFormatHTLMQ2(htmlContent), 0, 0,0, callback)

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
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        var htmlContent = ""

        try {


            htmlContent =
                "<pre>" +
                        "<font size=\"4\">" + "${getCurrentDate()}" + "</font><br><br><br>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_seller_equal_upper)} $sellerCode     M: ${getDeviceId()}" + "</font><br>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_stub_equal)}  $stubs${if (isRetry) {
                    R_string(R.string.print_retry_icon)
                } else {
                    ""
                }}" + "</font><br>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_check_equal)} $digitChecking" + "</font><br><br><br>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_betplay_recharge)}" + "</font><br><br><br>" +
                        "<font size=\"4\">" + "${R_string(R.string.print_document_text)}        : $documentId" + "</font><br>" +
                        "<font size=\"4\">" + "Valor Recarga: $${formatValue(value.toDouble())}" + "</font><br><br>" +
                        "</pre>"


        } catch (e: Exception) {
            e.printStackTrace()
            callback(PrinterStatus.ERROR)
            return
        }

        executeHtmlPrintQ2(htmlContent, 17, 0,3, callback)

    }
}