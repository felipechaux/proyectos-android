package co.com.pagatodo.core.util.print

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.print.PrinterStatus
import woyou.aidlservice.jiuiv5.ICallback
import woyou.aidlservice.jiuiv5.IWoyouService

import java.util.ArrayList
import java.util.LinkedList

class AidlUtil private constructor() {

    private var woyouService: IWoyouService? = null
    private var context: Context? = null

    val isConnect: Boolean
        get() = woyouService != null

    private val connService = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName) {
            woyouService = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            woyouService = IWoyouService.Stub.asInterface(service)
        }
    }

    /**
     * Configurar la densidad de impresión
     */
    private val darkness = intArrayOf(0x0600, 0x0500, 0x0400, 0x0300, 0x0200, 0x0100, 0, 0xffff, 0xfeff, 0xfdff, 0xfcff, 0xfbff, 0xfaff)

    /**
     * Obtener información del sistema de impresora, ponerlo en la lista
     *
     * @return list
     */
    //info.add(woyouService.getServiceVersion());
    val printerInfo: List<String>?
        get() {
            if (woyouService == null) {
                Log.e(context?.getString(R.string.toast_10), context?.getString(R.string.toast_2))
                return null
            }

            val info = ArrayList<String>()
            try {
                woyouService?.printerSerialNo?.let { info.add(it) }
                woyouService?.printerModal?.let { info.add(it) }
                woyouService?.printerVersion?.let { info.add(it) }
                info.add(woyouService?.printedLength.toString()+"")
                info.add("")
                val packageManager = context?.packageManager
                try {
                    val packageInfo = packageManager?.getPackageInfo(SERVICE_PACKAGE, 0)
                    if (packageInfo != null) {
                        info.add(packageInfo?.versionName)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            info.add("${packageInfo.longVersionCode}")
                        }
                        else {
                            info.add("${packageInfo?.versionCode}")
                        }
                    } else {
                        info.add("")
                        info.add("")
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }

            } catch (e: RemoteException) {
                e.printStackTrace()
            }

            return info
        }

    //Obtener el modo de impresión actual
    val printMode: Int?
        get() {
            if (woyouService == null) {
                Log.e(context?.getString(R.string.toast_10), context?.getString(R.string.toast_2))
                return -1
            }

            var res: Int? = -1
            try {
                woyouService?.printerMode?.let { res = it }
            } catch (e: RemoteException) {
                e.printStackTrace()
                res = -1
            }

            return res
        }

    /**
     * Servicio de conexion
     *
     * @param context context
     */
    fun connectPrinterService(context: Context) {
        this.context = context.applicationContext
        val intent = Intent()
        intent.setPackage(SERVICE_PACKAGE)
        intent.setAction(SERVICE_ACTION)
        context.applicationContext.startService(intent)
        context.applicationContext.bindService(intent, connService, Context.BIND_AUTO_CREATE)
    }

    /**
     * Servicio de desconexión
     *
     * @param context context
     */
    fun disconnectPrinterService(context: Context) {
        if (woyouService != null) {
            context.applicationContext.unbindService(connService)
            woyouService = null
        }
    }

    fun generateCB(printerCallback: PrinterCallback): ICallback {
        return object : ICallback.Stub() {
            @Throws(RemoteException::class)
            override fun onRunResult(isSuccess: Boolean, code: Int, msg: String) {

            }

        }
    }

    fun generateICallback(callback: (printerStatus: PrinterStatus) -> Unit?): ICallback {
        return object: ICallback.Stub() {
            @Throws(RemoteException::class)
            override fun onRunResult(isSuccess: Boolean, code: Int, msg: String?) {
                if (isSuccess) {
                    callback(PrinterStatus.OK)
                }
            }
        }
    }

    fun setDarkness(index: Int) {
        if (woyouService == null) {
            Log.e(context?.getString(R.string.toast_10), context?.getString(R.string.toast_2))
            return
        }

        val k = darkness[index]
        try {
            woyouService?.sendRAWData(ESCUtil.setPrinterDarkness(k), null)
            woyouService?.printerSelfChecking(null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    /**
     * Inicializar la impresora
     */
    fun initPrinter() {
        if (woyouService == null) {
            Log.e(context?.getString(R.string.toast_10), context?.getString(R.string.toast_2))
            return
        }

        try {
            woyouService?.printerInit(null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    /**
     * Imprimir código QR
     */
    fun printQr(data: String, modulesize: Int, errorlevel: Int) {
        if (woyouService == null) {
            Log.e(context?.getString(R.string.toast_10), context?.getString(R.string.toast_2))
            return
        }


        try {
            woyouService?.setAlignment(1, null)
            woyouService?.printQRCode(data, modulesize, errorlevel, null)
            woyouService?.lineWrap(3, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    /**
     * Imprimir código de barras
     */
    fun printBarCode(data: String, symbology: Int, height: Int, width: Int, textposition: Int) {
        if (woyouService == null) {
            Log.e(context?.getString(R.string.toast_10), context?.getString(R.string.toast_2))
            return
        }


        try {
            woyouService?.printBarCode(data, symbology, height, width, textposition, null)
            woyouService?.lineWrap(3, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    /**
     * Imprimir texto
     */
    fun printText(content: String, size: Float, isBold: Boolean, isUnderLine: Boolean) {
        if (woyouService == null) {
            Log.e(context?.getString(R.string.toast_10), context?.getString(R.string.toast_2))
            return
        }

        try {
            if (isBold) {
                woyouService?.sendRAWData(ESCUtil.boldOn(), null)
            } else {
                woyouService?.sendRAWData(ESCUtil.boldOff(), null)
            }

            if (isUnderLine) {
                woyouService?.sendRAWData(ESCUtil.underlineWithOneDotWidthOn(), null)
            } else {
                woyouService?.sendRAWData(ESCUtil.underlineOff(), null)
            }

            woyouService?.printTextWithFont(content, null, size, null)
            woyouService?.lineWrap(3, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    /*
     *Imprimir imagen
     */
    fun printBitmap(bitmap: Bitmap) {
        if (woyouService == null) {
            Log.e(context?.getString(R.string.toast_10), context?.getString(R.string.toast_2))
            return
        }

        try {
            woyouService?.setAlignment(1, null)
            woyouService?.printBitmap(bitmap, null)
            woyouService?.lineWrap(3, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    /**
     * Imprimir imágenes y texto en el orden especificado.
     */
    fun printBitmap(bitmap: Bitmap, orientation: Int) {
        if (woyouService == null) {
            Log.e(context?.getString(R.string.toast_10), context?.getString(R.string.toast_2))
            return
        }

        try {
            if (orientation == 0) {
                woyouService?.printBitmap(bitmap, null)
                woyouService?.printText("Arreglo horizontal\n", null)
                woyouService?.printBitmap(bitmap, null)
                woyouService?.printText("Arreglo horizontal\n", null)
            } else {
                woyouService?.printBitmap(bitmap, null)
                woyouService?.printText("\nArreglo vertical\n", null)
                woyouService?.printBitmap(bitmap, null)
                woyouService?.printText("\nArreglo vertical\n", null)
            }
            woyouService?.lineWrap(3, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }


    /**
     * Imprimir formulario
     */
    fun printTable(list: LinkedList<TableItem>) {
        if (woyouService == null) {
            Log.e(context?.getString(R.string.toast_10), context?.getString(R.string.toast_2))
            return
        }

        try {
            for (tableItem in list) {
                Log.i("kaltin", "printTable: " + tableItem.text!![0] + tableItem.text!![1] + tableItem.text!![2])
                woyouService?.printColumnsText(tableItem.text, tableItem.width, tableItem.align, null)
            }
            woyouService?.lineWrap(3, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    /*
     * Gratis tres lineas!
     */
    fun print3Line() {
        if (woyouService == null) {
            Log.e(context?.getString(R.string.toast_10), context?.getString(R.string.toast_2))
            return
        }

        try {
            woyouService?.lineWrap(3, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun sendRawData(data: ByteArray?, callback: (printerStatus: PrinterStatus) -> Unit?) {
        if (woyouService == null) {
            Log.e(context?.getString(R.string.toast_10), context?.getString(R.string.toast_2))
            return
        }

        try {

            val iCallback = generateICallback(callback)

            woyouService?.sendRAWData(byteArrayOf(0x1c, 0x43, 0xFF.toByte()), iCallback)
            woyouService?.setAlignment(0,iCallback)
            woyouService?.sendRAWData(data, iCallback)
            woyouService?.lineWrap(3, null)
            woyouService?.cutPaper(null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun printWithAlignment(lines: String, linesWithAlignment: Array<Int>, callback: (printerStatus: PrinterStatus) -> Unit?) {

        val left = 0
        val center = 1

        if (woyouService == null) {
            Log.e(context?.getString(R.string.toast_10), context?.getString(R.string.toast_2))
            return
        }

        try {

            val iCallback = generateICallback(callback)
            var index = 0

            lines.split("\n").forEach {
                woyouService?.setAlignment(left,null)
                if(linesWithAlignment.contains(index)){
                    woyouService?.setAlignment(center, null)
                }

                woyouService?.printText("$it\n", null)
                index++
            }

            woyouService?.lineWrap(3, null)
            woyouService?.cutPaper(iCallback)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    companion object {
        private val SERVICE_PACKAGE = "woyou.aidlservice.jiuiv5"
        private val SERVICE_ACTION = "woyou.aidlservice.jiuiv5.IWoyouService"
        val instance = AidlUtil()
    }
}