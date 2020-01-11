package co.com.pagatodo.core.data.print

import android.text.TextPaint
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.print.PrintManager.Companion.createBitmap
import co.com.pagatodo.core.data.print.PrintManager.Companion.createCanvas
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.data.print.PrintManager.Companion.setFontStyle
import co.com.pagatodo.core.data.print.bluetooth.PrintBluetoothManager
import co.com.pagatodo.core.util.SharedPreferencesUtil
import co.com.pagatodo.core.util.formatValue

class RechargePrint: BasePrint(), IRechargePrint {
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
        val bitmap= createBitmap()
        val canvas = createCanvas(bitmap)
        val paint = TextPaint()

        setFontStyle(paint, 2, false)
        canvas.drawText(getCurrentDate(),0F, 100F, paint)

        canvas.drawText("${RUtil.R_string(R.string.print_seller_equal_upper)} $sellerCode     M: ${getDeviceId()}",0F,180F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_stub_equal)}  $stubs${if(isRetry){
            RUtil.R_string(R.string.print_retry_icon)
        }else{""}}",0F,200F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_check_equal)} $digitChecking",0F,220F, paint)

        canvas.drawText(RUtil.R_string(R.string.print_betplay_recharge),0F,300F, paint)

        canvas.drawText("${RUtil.R_string(R.string.print_document_text)}        : $documentId",0F,380F, paint)
        canvas.drawText("${RUtil.R_string(R.string.print_recharge_value)}     : $value",0F,400F, paint)

        PrintBluetoothManager.printBitmap(bitmap, callback)
    }

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
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        val bitmap= createBitmap()
        val canvas = createCanvas(bitmap)
        val paint = TextPaint()

        val sellerCode = SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_seller_code))
        val municipalityCode =
            SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_municipality_code))
        val officeCode = SharedPreferencesUtil.getPreference<String>(RUtil.R_string(R.string.shared_key_office_code))
        val imei = getDeviceId()

        setFontStyle(paint, 2, true)

        canvas.drawText("$sellerCode $municipalityCode $officeCode $imei",0F, 120F, paint)
        canvas.drawText("Colilla: $stub",0F,145F, paint)
        canvas.drawText("No Factura: $billPrefix-$billNumber",0F,170F, paint)
        canvas.drawText("Fecha: $transactionDate",0F, 195F, paint)
        canvas.drawText("Hora: $transactionHour",0F,220F, paint)
        canvas.drawText("Línea Recargada: $number",0F,245F, paint)
        canvas.drawText("Valor Recarga: $${formatValue(value.toDouble())}",0F,270F, paint)
        canvas.drawText("-$billPrefix",0F,295F, paint)

        canvas.drawText(billMessage,0F,345F, paint)

        PrintBluetoothManager.printBitmap(bitmap, callback)
    }
}