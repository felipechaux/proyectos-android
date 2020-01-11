package co.com.pagatodo.core.util.print.tmu

import android.content.Context
import android.util.Log
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil
import com.epson.epos2.Epos2Exception
import com.epson.epos2.discovery.Discovery
import com.epson.epos2.discovery.FilterOption
import com.epson.epos2.printer.Printer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.concurrent.schedule

typealias EpsonDiscoverPrinters = (epsonDevice: EpsonDevice?) -> Unit

class EpsonPrinter {

    var mPrinter: Printer? = null
    lateinit var finishedCallback: (printerStatus: PrinterStatus) -> Unit?

    fun print(data: String, finishedCallback: (printerStatus: PrinterStatus) -> Unit? = {}) {
        this.finishedCallback = finishedCallback
        initializeObject { printer, statusInit ->
            if (statusInit) {
                val target =
                    SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_jsa_printer))
                printString(data, target, printer)
            }
        }
    }

    private fun printString(data: String, target: String, printer: Printer?) {
        printer?.let { printerItem ->
            val pConnect = connectPrinter(target)
            if (pConnect.first) {
                val printerStatus = printerItem.status
                if (printerStatus.online == Printer.TRUE
                    && printerStatus.connection == Printer.TRUE
                ) {
                    try {
                        printerItem.addTextAlign(Printer.ALIGN_LEFT)
                        printerItem.addText(data)
                        printerItem.addFeedLine(1)
                        printerItem.addCut(Printer.CUT_FEED)
                        printerItem.sendData(Printer.PARAM_DEFAULT)
                        Log.i("EPSON", "Printer Send Data")
                        Timer().schedule(4000) {
                            disconnectPrinter()
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                        tryToDisconnect(printerItem)
                    }
                } else {
                    Log.i("EPSON", "tryToDisconnect: Printer not connected")
                    tryToDisconnect(printerItem)
                }
            }
        }
    }

    fun printBaloto(
        data: String,
        headerBaloto: String,
        finishedCallback: (printerStatus: PrinterStatus) -> Unit? = {}
    ) {
        this.finishedCallback = finishedCallback
        initializeObject { printer, statusInit ->
            if (statusInit) {
                val target =
                    SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_jsa_printer))
                printStringBaloto(data, headerBaloto, target, printer)
            }
        }
    }

    private fun printStringBaloto(
        data: String,
        headerBaloto: String?,
        target: String,
        printer: Printer?
    ) {
        printer?.let { printerItem ->
            val pConnect = connectPrinter(target)
            if (pConnect.first) {
                val printerStatus = printerItem.status
                if (printerStatus.online == Printer.TRUE
                    && printerStatus.connection == Printer.TRUE
                ) {
                    try {

                        printerItem.addTextAlign(Printer.ALIGN_CENTER)
                        printerItem.addText(headerBaloto)

                        printerItem.addTextAlign(Printer.ALIGN_LEFT)
                        printerItem.addText(data)
                        printerItem.addFeedLine(1)
                        printerItem.addCut(Printer.CUT_FEED)
                        printerItem.sendData(Printer.PARAM_DEFAULT)
                        Log.i("EPSON", "Printer Send Data")

                        Timer().schedule(8000) {
                            disconnectPrinter()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        tryToDisconnect(printerItem)
                    }
                } else {
                    Log.i("EPSON", "tryToDisconnect: Printer not connected")
                    tryToDisconnect(printerItem)
                }
            }
        }
    }

    private fun tryToDisconnect(printer: Printer) {
        try {
            printer.disconnect()
        } catch (e: Exception) {
            val msg = e.message
            Log.i("EPSON", "tryToDisconnect: $msg")
        }
    }

    private fun initializeObject(closure: (printer: Printer?, status: Boolean) -> Unit) {
        try {
            mPrinter =
                Printer(Printer.TM_U220, Printer.MODEL_ANK, PagaTodoApplication.getAppContext())
            closure(mPrinter, true)
        } catch (e: Exception) {
            closure(mPrinter, false)
        }
    }

    private fun connectPrinter(target: String): Pair<Boolean, String> {

        var status = true
        var message = "Success"
        var isBeginTransaction = false

        if (mPrinter == null) {
            status = false
            message = "Printer could not instanciate"
            Log.i("EPSON", message)
            return Pair(status, message)
        }

        try {
            Log.i("EPSON", "Connecting: ${target}")
            mPrinter?.connect(target, Printer.PARAM_DEFAULT)
        } catch (e: Epos2Exception) {
            Log.i("EPSON", "Connecting Error: ${e.errorStatus}")
            status = false
            message = "Error when connecting: ${e.message}"
            Log.i("EPSON", message)
            return Pair(status, message)
        }

        try {
            mPrinter?.beginTransaction()
            isBeginTransaction = true
        } catch (e: Exception) {
            status = false
            message = "Error when starting the transaction"
            Log.i("EPSON", message)
        }

        if (!isBeginTransaction) {
            try {
                mPrinter?.disconnect()
            } catch (e: Epos2Exception) {
                status = false
                message = "An error occurred while trying to disconnect"
                Log.i("EPSON", message)
                return Pair(status, message)
            }
        }

        Log.i("EPSON", "connectPrinter: ${if (status) "OK" else "ERROR"} - ${message}")
        return Pair(status, message)
    }

    private fun disconnectPrinter() {
        if (mPrinter == null) {
            return
        }

        try {
            mPrinter?.endTransaction()
        } catch (e: Exception) {
            Log.i("EPSON", "Error End Transaction: ${mPrinter}")
        }

        try {
            mPrinter?.disconnect()
        } catch (e: Exception) {
            Log.i("EPSON", "Error disconnect: ${e.message}")
        }
        finalizeObject()
    }

    private fun finalizeObject() {
        mPrinter?.let {
            mPrinter?.clearCommandBuffer()
            mPrinter?.setReceiveEventListener(null)
            mPrinter = null
            finishedCallback(PrinterStatus.OK)
        }
    }

    companion object {
        fun discoverPrinters(context: Context, callback: EpsonDiscoverPrinters = {}) {
            val mFilterOption = FilterOption().apply {
                deviceType = Discovery.TYPE_PRINTER
                epsonFilter = Discovery.FILTER_NAME
            }
            try {
                Discovery.start(context, mFilterOption) { deviceInfo ->
                    GlobalScope.launch(Dispatchers.IO) {
                        Discovery.stop()
                        withContext(Dispatchers.Main) {
                            callback(EpsonDevice(deviceInfo.deviceName, deviceInfo.target))
                        }
                    }
                }
            } catch (e: Epos2Exception) {
                e.printStackTrace()
                GlobalScope.launch(Dispatchers.Main) {
                    callback(null)
                }
            }
        }
    }
}

data class EpsonDevice(
    var name: String, var target: String
)