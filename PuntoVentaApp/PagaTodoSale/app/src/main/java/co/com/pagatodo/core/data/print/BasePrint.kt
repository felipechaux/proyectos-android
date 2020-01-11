package co.com.pagatodo.core.data.print

import android.util.Log
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.PagaTodoApplication.Companion.format
import co.com.pagatodo.core.PagaTodoApplication.Companion.printerDevice
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.print.BasePrintModel
import co.com.pagatodo.core.data.print.PrintManager.Companion.setFontStyle
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.DeviceUtil
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil
import com.cloudpos.DeviceException
import com.cloudpos.POSTerminal
import com.cloudpos.printer.Format
import com.cloudpos.printer.PrinterDevice
import com.pos.device.printer.PrintCanvas
import java.util.*
import com.cloudpos.jniinterface.RFCardInterface.restore
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.opengl.ETC1.getHeight
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.graphics.*
import android.os.Handler
import android.os.Looper
import co.com.pagatodo.core.data.model.print.VirtualSoatPrintModel
import com.wizarpos.htmllibrary.PrinterHtmlListener


open class BasePrint {
    protected fun getStubs(): String {
        val serie1 =
            SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_current_serie1))
        val serie2 =
            SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_current_serie2))
        return "$serie1  $serie2"
    }

    protected fun getCurrentDate(): String {
        val abrName = DateUtil.getCurrentAbrNameOfMonth()
        val day = DateUtil.getCurrentDay()
        val yearWithHour = DateUtil.convertDateToStringFormat(
            DateUtil.StringFormat.YYYYHOUR_SPLIT_WHITE_SPACE,
            DateUtil.getCurrentDate()
        )
        return "$abrName $day,     $yearWithHour"
    }

    protected fun getCurrentDate(date: Date): String {
        val abrName = DateUtil.getCurrentAbrNameOfMonth(date)
        val day = DateUtil.getCurrentDay(date)
        val yearWithHour = DateUtil.convertDateToStringFormat(
            DateUtil.StringFormat.YYYYHOUR_SPLIT_WHITE_SPACE,
            date
        )
        return "$abrName $day,$yearWithHour"
    }

    protected fun getCurrentDate(stringFormat: DateUtil.StringFormat) =
        DateUtil.convertDateToStringFormat(stringFormat, DateUtil.getCurrentDate())

    protected fun getMaskedSellerCode(): String {
        val sellerCode =
            SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_seller_code))
        return "***".plus(sellerCode.substring(3))
    }

    protected fun getSuperChanceLotteriesWithPrintFormat(lotteries: List<LotteryModel>?): String {
        val response = StringBuilder()
        lotteries?.forEachIndexed { index, lottery ->
            if (DeviceUtil.isSalePoint()) {
                response.append("${lottery.name}-")
            } else {
                val whiteSpace = if (index == 1) "            " else " "
                response.append(" ${lottery.name} -$whiteSpace")
            }
        }
        return response.toString().removePrefix(" ").dropLast(2)
    }

    protected fun getSuperChanceLotteriesWithPrintFormatQ22(lotteries: List<LotteryModel>?): String {
        val response = StringBuilder()
        var responsereturn = ""
        lotteries?.forEachIndexed { index, lottery ->

            response.append("${lottery.name}-")


            if (index == 2 || index == 6 || index == 10 || index == 14 || index == 18 || index == 22) {
                response.append("<br>")
            }
        }
        responsereturn = response.toString()
        if (responsereturn.substring(responsereturn.length - 1) == "-")
            responsereturn = response.toString().dropLast(1)

        return responsereturn
    }

    protected fun getSuperChanceLotteriesWithPrintFormatQ2(lotteries: List<LotteryModel>?): String {
        val response = StringBuilder()
        lotteries?.forEachIndexed { index, lottery ->
            if (DeviceUtil.isSalePoint()) {
                response.append("${lottery.name}-")
            } else {
                val whiteSpace = if (index == 1) "\n" else " "
                response.append(" ${lottery.name}-$whiteSpace")

            }
        }

        return response.toString().removePrefix(" ").dropLast(2)
    }

    protected fun getLotteriesWithPrintFormat(
        lotteries: List<LotteryModel>?,
        mod: Int? = 0
    ): String {
        val response = StringBuilder()
        var responsereturn = ""
        lotteries?.forEachIndexed { index, lottery ->
            val index2: Int = index + 1
            if (DeviceUtil.isSalePoint()) {
                val newLine = if (index2 % 6 == 0) "\n" else ""
                response.append("${lottery.name}-$newLine")
                responsereturn = response.toString().substring(0, response.length - 1)
            } else {
                var whiteSpace = ""
                whiteSpace = if (index2 % mod!! == 0) "<br>" else ""
                response.append(" ${lottery.name}-$whiteSpace")
                responsereturn = response.toString()
            }
        }
        if (responsereturn.substring(responsereturn.length - 1) == ">")
            responsereturn = response.toString().dropLast(5)
        else
            responsereturn = response.toString().dropLast(1)
        return responsereturn
    }

    protected fun printHeader(
        printModel: BasePrintModel,
        canvas: PrintCanvas,
        paint: Paint,
        isRetry: Boolean
    ) {
        setFontStyle(paint, 2, true)
        canvas.drawText("               ${getCurrentDate()}", paint)
        canvas.drawText(
            "${R_string(R.string.print_stub_equal)}  ${printModel.stubs}${if (isRetry) {
                R_string(R.string.print_retry_icon)
            } else {
                ""
            }}", paint
        )
        canvas.drawText(
            "${R_string(R.string.print_unique_serie_equal)} ${printModel.uniqueSerial}",
            paint
        )
        canvas.drawText(
            "${R_string(R.string.print_check_equal)} ${printModel.digitChecking}",
            paint
        )
        canvas.drawText(
            "${R_string(R.string.print_seller_equal_upper)} ${printModel.consultant}     M: ${getDeviceId()}",
            paint
        )
        canvas.drawText(
            "${R_string(R.string.print_raffle_date_equal)} ${printModel.raffleDate}",
            paint
        )
    }

    protected fun getDeviceId(): String {
        var deviceId = ""

        if (DeviceUtil.isSalePoint()) {
            deviceId = DeviceUtil.getSerialNumber() ?: ""
        } else {
            val imei = DeviceUtil.getDeviceIMEI()
            val index = (imei?.length ?: 0) - 8
            deviceId = imei?.substring(index, (DeviceUtil.getDeviceIMEI()?.length ?: index)) ?: ""
        }

        return deviceId
    }

    protected fun openPrinter(): Any {

        var ret: Int

        try {

            ret = validarImpresora()

            if (ret != 0) {
                Log.e("STATUS: ", "printChance device baterry error")
                ret = 1
            } else {
                printerDevice =
                    POSTerminal.getInstance(PagaTodoApplication.getAppContext()).getDevice(
                        "cloudpos.device.printer"
                    ) as PrinterDevice

                printerDevice.open()

                format = Format()
                ret = 0
            }
        } catch (de: java.lang.Exception) {
            de.printStackTrace()
            ret = 1
        }

        return ret

    }

    private fun validarImpresora(): Int {

        val ret: Int
        printerDevice =
            POSTerminal.getInstance(PagaTodoApplication.getAppContext()).getDevice(
                "cloudpos.device.printer"
            ) as PrinterDevice

        printerDevice.open()

        if (printerDevice.queryStatus() != PrinterDevice.STATUS_PAPER_EXIST) {
            ret = 1
        } else {
            ret = 0
        }

        printerDevice.close()

        return ret

    }


    protected fun finalizePrinter(): Boolean {
        try {
            printerDevice.close()
            return true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * Método usado para agregar varias líneas en blanco dentro de la impresión
     */
    protected fun addLineBreak(numberLineBreak: Int) {

        try {
            for (index in 1..numberLineBreak) {
                printerDevice.printText("\n")
            }
        } catch (err: java.lang.Exception) {
            Log.e("PRINT-FORMAT", "addLineBreak")
        }

    }

    /**
     * Método usado para asignar el tamaño y tipo de fuente en la impresión en la Q2
     */
    fun setFontStyle(format: Format?, size: Int, isBold: Boolean, align: String) {

        format?.clear()
        format?.setParameter(Format.FORMAT_ALIGN, align)

        if (isBold)
            format?.setParameter(Format.FORMAT_FONT_BOLD, Format.FORMAT_FONT_VAL_TRUE)
        else
            format?.setParameter(Format.FORMAT_FONT_BOLD, Format.FORMAT_FONT_VAL_FALSE)

        when (size) {
            0 -> {
            }
            1 -> format?.setParameter(Format.FORMAT_FONT_SIZE, Format.FORMAT_FONT_SIZE_EXTRASMALL)
            2 -> format?.setParameter(Format.FORMAT_FONT_SIZE, Format.FORMAT_FONT_SIZE_SMALL)
            3 -> format?.setParameter(Format.FORMAT_FONT_SIZE, Format.FORMAT_FONT_SIZE_MEDIUM)
            4 -> format?.setParameter(Format.FORMAT_FONT_SIZE, Format.FORMAT_FONT_SIZE_LARGE)
            5 -> format?.setParameter(Format.FORMAT_FONT_SIZE, Format.FORMAT_FONT_SIZE_EXTRALARGE)
            else -> {
            }
        }
    }

    /***************************
    Método       : lpt_format
    Description  : Añade el tamaño de la fuente de la impresión
    Input        : fuente    = Tamaño de la fuente
    negrilla  = Fuente con negrilla
    ancho     = Ancho de la fuente
    alto      = Alto de la fuente
    reducia   = Fiente reducida
    inverso   = Inverso de la fuente
    subrayado = Fuente subrayada
    Return       : Ninguno
     ***************************/
    fun lptFormat(
        fuente: Int, negrilla: Int, ancho: Int, alto: Int,
        reducida: Int, inverso: Int, subrayado: Int
    ) {

        try {

            format?.setParameter("align", "left")   // Lineado

            if (negrilla == 1) {
                format?.setParameter("bold", "true")
                val cmd = byteArrayOf(0x1B.toByte(), 0x21.toByte(), 8)
                PagaTodoApplication.printerDevice.sendESCCommand(cmd)
            } else {
                format?.setParameter("bold", "false")
                val cmd = byteArrayOf(0x1B.toByte(), 0x21.toByte(), 0)
                PagaTodoApplication.printerDevice.sendESCCommand(cmd)
            }

            when (fuente) {
                // Normal
                0 -> {
                    format?.setParameter("size", "medium")
//                Global.lengthLine = 32
                }
                // Small
                1 -> {
                    format?.setParameter("size", "small")
//                Global.lengthLine = 42
                }
                // Big
                2 -> {
                    format?.setParameter("size", "large")
//                Global.lengthLine = 16
                }
                // Extra Big
                3 -> {
                    format?.setParameter("size", "extra-large")
//                Global.lengthLine = 10
                }
                // Extra small
                4 -> {
                    format?.setParameter("align", "center")
                    format?.setParameter("size", "extra-small")
                }

                else -> {
                    format?.setParameter("size", "medium")
//                Global.lengthLine = 32
                }
            }//Global.lengthLine = 42;

            if (subrayado == 1) {
                val cmd = byteArrayOf(0x1B.toByte(), 0x21.toByte(), 128.toByte())
                PagaTodoApplication.printerDevice.sendESCCommand(cmd)
            } else {
                val cmd = byteArrayOf(0x1B.toByte(), 0x21.toByte(), 0.toByte())
                PagaTodoApplication.printerDevice.sendESCCommand(cmd)
            }


        } catch (err: Exception) {
            Log.e("PRINT-FORMAT", "lptFormat")
        }

    }

    /**********
     * Function        : lpt_feed_dots
     * Description     : imprime saltos de linea por pixeles
     * Input           : count= cantidad de saltos
     * Return          : Nothing
     */

    fun lpt_feed_dots(count: Int) {

        try {
            val cmd = byteArrayOf(0x1B.toByte(), 0x4A.toByte(), count.toByte())
            printerDevice.sendESCCommand(cmd)

        } catch (err: java.lang.Exception) {
            Log.e("PRINT-FORMAT", "lpt_feed_dots")

        }

    }

    fun drawTextQ2(
        text: String,
        textWidth: Int,
        textSize: Float,
        bold: Boolean,
        typeface: Typeface,
        tipo: Int
    ): Bitmap {

        // Get text dimensions
        val textPaint = TextPaint(ANTI_ALIAS_FLAG or Paint.LINEAR_TEXT_FLAG)

        textPaint.color = Color.BLACK
        textPaint.textSize = textSize
        textPaint.typeface = typeface
        textPaint.isFakeBoldText = bold
        val mTextLayout = StaticLayout(
            text, textPaint,
            textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false
        )

        // Create bitmap and canvas to draw to
        val b = Bitmap.createBitmap(textWidth, mTextLayout.height, Bitmap.Config.RGB_565)
        val canvas = Canvas(b)

        // Draw background
        val paint = Paint(ANTI_ALIAS_FLAG or Paint.LINEAR_TEXT_FLAG)
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.textSize = 18f
        canvas.drawPaint(paint)

        // Draw text
        canvas.save()
        if (tipo == 0)
            canvas.translate(20f, 0f)
        else
            canvas.translate(0f, 20f)

        mTextLayout.draw(canvas)
        canvas.restore()

        return b
    }

    /* plantilla html con formato de impresion*/
    protected fun generatePrintFormatHTLMQ2(content: String): String {
        val builder = StringBuilder()

        builder.append("<div style =\"overflow: hidden;height:565.6px\">")
        builder.append("<br>")
        builder.append("<br>")
        builder.append("<br>")
        builder.append(content)
        builder.append("</div>")

        return builder.toString()
    }

    /* GENERAR IMPRESION DE SOAT*/
    protected fun generatePrintSoat(printModel: VirtualSoatPrintModel): String {

        val stringBuilder = StringBuilder()
        stringBuilder.clear()

        val header = printModel.header?.split("\\n") ?: arrayListOf()
        stringBuilder.append("<p align=\"center\"  >${header[0]}</p>")
        stringBuilder.append("<p align=\"center\"  >${header[1]}</p>")
        stringBuilder.append("</br>")

        stringBuilder.append("<p>RECAUDO DE POLIZA SOAT</p>")
        stringBuilder.append("<p>Fecha Hora de Expedición:</p>")

        val dateSys = DateUtil.convertDateToStringFormat(
            DateUtil.StringFormat.DDMMYY_HHMMA_SPLIT_BACKSLASH,
            Date()
        )
        stringBuilder.append("<p>$dateSys")
        stringBuilder.append(
            "<p>Fecha Hora de Inicio Vigencia:\n${DateUtil.convertDateToStringFormat(
                DateUtil.StringFormat.DDMMYY_HHMMA_SPLIT_BACKSLASH,
                Date(printModel.beginValidityDateHour!!.toLong())
            )}</p>"
        )
        stringBuilder.append(
            "<p>Fecha Hora de Fin Vigencia:\n${DateUtil.convertDateToStringFormat(
                DateUtil.StringFormat.DDMMYY_HHMMA_SPLIT_BACKSLASH,
                Date(printModel.endValidityDateHour!!.toLong())
            )}</p>"
        )


        stringBuilder.append("<p>No Poliza: ${printModel.policyNumber}</p>")
        stringBuilder.append("<p>Prima SOAT:\n$${printModel.soatValue}</p>")
        stringBuilder.append("<p>Contribucion Fosyga:\n$${printModel.fosygaValue}</p>")
        stringBuilder.append("<p>Tarifa RUNT:\n$${printModel.runtValue}</p>")
        stringBuilder.append("<p>Total a pagar:\n$${printModel.totalValue}</p>")
        stringBuilder.append("<p>------</p>")
        stringBuilder.append("<p>INFORMACION DEL TOMADOR</p>")
        stringBuilder.append("<p>Nombres y Apellidos del Tomador:\n${printModel.takerNameLast}</p>")
        stringBuilder.append("<p>Tipo Documento:\n${printModel.documentType}</p>")
        stringBuilder.append("<p>Num. Documento:\n${printModel.takerDocumentNumber}</p>")
        stringBuilder.append("<p>Ciudad Residencia Tomador:\n${printModel.takerCity}</p>")
        stringBuilder.append("<p>INFORMACION DEL VEHICULO</p>")
        stringBuilder.append("<p>Clase Vehiculo: ${printModel.vehicleType}</p>")
        stringBuilder.append("<p>Servicio: ${printModel.service}</p>")
        stringBuilder.append("<p>Cilindraje: ${printModel.cylinderCapacity}</p>")
        stringBuilder.append("<p>Modelo: ${printModel.vehicleModel}</p>")
        stringBuilder.append("<p>Placa: ${printModel.licensePlate}</p>")
        stringBuilder.append("<p>Marca: </p>")
        stringBuilder.append("<p>Linea: ${printModel.vehicleLine}</p>")
        stringBuilder.append("<p>No Motor: ${printModel.motorNumber}</p>")
        stringBuilder.append("<p>No Chasis: ${printModel.chassisNumber}</p>")
        stringBuilder.append("<p>No VIN: ${printModel.vinNumber}</p>")
        stringBuilder.append("<p>Pasajeros: ${printModel.passengers}</p>")
        stringBuilder.append("<p>Capacidad: ${printModel.capacity}</p>")
        stringBuilder.append("<p>INFORMACION ADICIONAL</p>")
        stringBuilder.append("<p>Nombre del punto del Recaudador: ------</p>")
        stringBuilder.append("<p>Codigo del punto del Recaudador: ------</p>")
        stringBuilder.append("<p>Ciudad: ------")
        stringBuilder.append("<p>Movimiento: ${printModel.transactionQuantity}</p>")
        stringBuilder.append("<p>------</p>")

        return stringBuilder.toString()

    }

    protected fun executeHtmlPrintQ2(
        htmlString: String,
        lineBreak: Int = 0,
        lptFeedDots: Int = 0,
        lineHeader: Int = 0,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {


        try {

            if (openPrinter() != 0) {
                callback(PrinterStatus.BUSY)
                return
            }

            if (lineBreak != 0)
                addLineBreak(lineHeader)


            printerDevice.printHTML(
                PagaTodoApplication.getAppContext(),
                htmlString,
                object : PrinterHtmlListener {
                    override fun onGet(bitmap: Bitmap, i: Int) {
                        // not implemented
                    }

                    override fun onFinishPrinting(result: Int) {
                        when (result) {
                            PrinterHtmlListener.PRINT_SUCCESS -> {
                                if (lineBreak != 0)
                                    addLineBreak(lineBreak)

                                if (lptFeedDots != 0)
                                    lpt_feed_dots(lptFeedDots)

                                executeCallBack(PrinterStatus.OK, callback)
                            }
                            PrinterHtmlListener.PRINT_ERROR -> executeCallBack(PrinterStatus.ERROR, callback)
                        }

                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            executeCallBack(PrinterStatus.ERROR, callback)
        }

    }


    fun executeCallBack( printerStatus: PrinterStatus,callback: (printerStatus: PrinterStatus) -> Unit?){

        finalizePrinter{
            callback(printerStatus)
        }

    }


    protected fun finalizePrinter(callback:() -> Unit?) {
        try {
            printerDevice.close()

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        callback()
    }

}