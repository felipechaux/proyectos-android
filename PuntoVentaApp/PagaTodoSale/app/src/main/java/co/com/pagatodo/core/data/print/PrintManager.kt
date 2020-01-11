package co.com.pagatodo.core.data.print

import android.graphics.*
import android.util.Log
import com.pos.device.printer.PrintCanvas
import com.pos.device.printer.PrintTask
import com.pos.device.printer.Printer
import com.pos.device.printer.PrinterCallback

/**
 * Clase administradora de la impresión
 */
class PrintManager {

    private var mInstance: PrintManager? = null

    /**
     * Método utilizado para obtener la instancia de la clase
     */
    fun getInstance(): PrintManager {
        if (null == mInstance) {
            mInstance = PrintManager()
        }
        return mInstance as PrintManager
    }

    companion object {

        private val bitmapWidth = 375
        private val bitmapHeight = 710

        private var printTask: PrintTask? = null
        private var printer: Printer? = null

        /**
         * Método utilizado para obtener el estado de la impresora
         */
        fun checkPrinterStatus(): Int {
            var ret: Int
            try {
                val t0 = System.currentTimeMillis()
                loop@ while (true) {
                    if (System.currentTimeMillis() - t0 > 30000) {
                        Log.e("STATUS: ", "printChance device busy")
                        ret = -1
                        break
                    }

                    ret = Printer.getInstance().status
                    when(ret){
                        Printer.PRINTER_OK -> {
                            Log.e("STATUS: ", "printChance device work")
                            break@loop
                        }
                        Printer.PRINTER_STATUS_BUSY -> {
                            Log.e("STATUS: ", "printChance device busy")
                            try {
                                Thread.sleep(200)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                            break@loop
                        }
                        Printer.PRINTER_STATUS_PAPER_LACK -> {
                            Log.e("STATUS: ", "printChance device lack of paper...")
                            break@loop
                        }
                        Printer.PRINTER_STATUS_NO_BATTERY -> {
                            Log.e("STATUS: ", "printChance device baterry error")
                            break@loop
                        }
                        else -> {
                            break@loop
                        }
                    }
                }
            }
            catch (e: NoClassDefFoundError) {
                ret = -1
            }

            return ret
        }

        /**
         * Método utilizado para realizar la impresión
         */
        fun print(pCanvas: PrintCanvas, callback: (printerStatus: PrinterStatus) -> Unit?) {
            val print = getPrintTaskInstance()
            print?.setPrintCanvas(pCanvas)
            // Iniciar la impresión
            getPrinterInstance()?.startPrint(print, object : PrinterCallback {
                override fun onResult(p0: Int, p1: PrintTask?) {
                    if (p0 == Printer.PRINTER_OK){
                        // En caso de que la impresión sea exitosa se enviar el código respectivo
                        callback(PrinterStatus.OK)
                    }else{
                        // En caso de que la impresión no sea exitosa se envía el estado de ésta
                        val printerStatus = when(p0) {
                            1 -> PrinterStatus.BUSY
                            2 -> PrinterStatus.BUSY
                            else -> PrinterStatus.UNKNOWN
                        }
                        callback(printerStatus)
                    }
                }
            })
        }

        /**
         * Método usado para obtener la instancia de la tarea de impresión
         */
        private fun getPrintTaskInstance(): PrintTask? {
            if (printTask == null)
                printTask = PrintTask()
            return printTask
        }

        /**
         * Método usado para obtener la instancia de la variable de impresión
         */
        fun getPrinterInstance(): Printer? {
            if (printer == null)
                printer = Printer.getInstance()
            return printer
        }

        /**
         * Método usado para asignar el tamaño y tipo de fuente en la impresión
         */
        fun setFontStyle(paint: Paint, size: Int, isBold: Boolean) {
            if (isBold)
                paint.typeface = Typeface.DEFAULT_BOLD
            else
                paint.typeface = Typeface.DEFAULT
            when (size) {
                0 -> {
                }
                1 -> paint.textSize = 16f
                2 -> paint.textSize = 22f
                3 -> paint.textSize = 30f
                else -> {
                }
            }
        }

        /**
         * Método usado para agregar el espacio necesario en el header de la impresión
         */
        fun addLineBreakHeaderPaper(canvas: PrintCanvas, paint: Paint) {
            for (index in 1..4) {
                addLineBreak(canvas, paint)
            }
        }

        /**
         * Método usado para agregar varias líneas en blanco dentro de la impresión
         */
        fun addLineBreak(canvas: PrintCanvas, paint: Paint, numberLineBreak: Int) {
            for (index in 1..numberLineBreak) {
                addLineBreak(canvas, paint)
            }
        }

        /**
         * Método usado para agregar una sola línea en blanco dentro de la impresión
         */
        fun addLineBreak(canvas: PrintCanvas, paint: Paint) {
            canvas.drawText(" ", paint)
        }

        fun createBitmap(): Bitmap {
            return Bitmap.createBitmap(bitmapWidth,bitmapHeight, Bitmap.Config.ARGB_8888)
        }

        fun createCanvas(bitmap: Bitmap): Canvas {
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.WHITE)
            return canvas
        }
    }
}