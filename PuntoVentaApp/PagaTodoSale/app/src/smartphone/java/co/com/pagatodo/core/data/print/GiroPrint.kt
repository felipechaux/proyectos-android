package co.com.pagatodo.core.data.print

import android.graphics.Paint
import co.com.pagatodo.core.R
import co.com.pagatodo.core.util.RUtil
import com.pos.device.printer.PrintCanvas


class GiroPrint : IGiroPrint {
  
    override fun printPaymentGiro(data: List<String>, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        // not implemented
    }

    override fun printCaptureGiro(data: List<String>, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        // not implemented
    }


    fun validateCenter(line:String):Boolean{

        return line.split(RUtil.R_string(R.string.giro_print_format))[0] == RUtil.R_string(R.string.giro_print_aling_center)
    }

    fun getLine(line:String):String{

        return line.split(RUtil.R_string(R.string.giro_print_format))[1]
    }


}