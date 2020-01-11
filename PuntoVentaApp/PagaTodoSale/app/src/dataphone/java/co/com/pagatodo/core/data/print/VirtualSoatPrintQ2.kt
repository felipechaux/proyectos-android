package co.com.pagatodo.core.data.print

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.data.model.print.VirtualSoatPrintModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.print.POSPrinter
import com.cloudpos.DeviceException
import com.wizarpos.htmllibrary.PrinterHtmlListener
import java.lang.Exception
import java.util.*

class VirtualSoatPrintQ2: BasePrint(), IVirtualSoatPrint {

    override fun print(printModel: VirtualSoatPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?) {

        executeHtmlPrintQ2(generatePrintSoat(printModel),13,0,0,callback)

    }

}