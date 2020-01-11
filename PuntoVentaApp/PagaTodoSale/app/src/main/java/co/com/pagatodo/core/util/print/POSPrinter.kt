package co.com.pagatodo.core.util.print

import android.util.Log
import co.com.pagatodo.core.data.print.PrinterStatus

class POSPrinter {

    fun print(textPrint: String, callback: (printerStatus: PrinterStatus) -> Unit? = {}) {

        val rv: ByteArray?
        rv = textPrint.toByteArray()
        sendData(rv, callback)
    }

    fun printWithAlignment(textPrint: String, linesWithAlignment: Array<Int>, callback: (printerStatus: PrinterStatus) -> Unit?){
        AidlUtil.instance.printWithAlignment(textPrint, linesWithAlignment, callback)
    }

    fun sendData(send: ByteArray?, callback: (printerStatus: PrinterStatus) -> Unit?) {
        AidlUtil.instance.sendRawData(send, callback)
    }
}