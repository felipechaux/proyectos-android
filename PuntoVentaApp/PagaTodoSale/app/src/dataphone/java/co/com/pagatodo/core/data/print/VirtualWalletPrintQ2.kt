package co.com.pagatodo.core.data.print

import android.graphics.Bitmap
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.print.VirtualWalletPrintModel
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.formatValue
import com.cloudpos.DeviceException
import com.pos.device.printer.PrintCanvas
import com.wizarpos.htmllibrary.PrinterHtmlListener

class VirtualWalletPrintQ2 : BasePrint(), IVirtualWalletPrint {

    val stringBuilder = StringBuilder()

    override fun print(printModel: VirtualWalletPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?) {



        val sbPrintData = StringBuilder()

        sbPrintData.append("<style>")
        sbPrintData.append("p.small {line-height: 0.1;}")
        sbPrintData.append("p.big {line-height: 1.8;}")
        sbPrintData.append("</style>")

        val header = printModel.textHeader?.split("\\n")
        val body = printModel.textBody?.split("\\n")
        sbPrintData.append("</br>")
        sbPrintData.append("</br>")
        header?.forEachIndexed { _, item ->

            sbPrintData.append("<p  align=\"center\" class=\"big\">${item}</p>")
        }

        body?.forEachIndexed { _, item ->
            sbPrintData.append("<p align=\"center\" class=\"big\">${item}</p>")
        }

        sbPrintData.append("<font face=\"verdana\" size=\"4\">" )

        sbPrintData.append("<p class=\"big\">Pin: ${printModel.pin}</p>")
        sbPrintData.append("<p class=\"big\">Usuario: ${getPreference<String>(R_string(R.string.shared_key_seller_code))}</p>")
        sbPrintData.append("<p class=\"big\">Valor Pin: $${formatValue(printModel.pinValue?.toDouble()?:0.0)}</p>")
        sbPrintData.append("</font>")

        executeHtmlPrintQ2("$sbPrintData",7,0,0,callback)

    }



}