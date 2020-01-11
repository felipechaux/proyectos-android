package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.print.ElderlyPrintModel
import co.com.pagatodo.core.util.print.POSPrinter
import java.lang.Exception

class ElderlyPrint:BasePrint(),IElderlyPrint {

    private var posPrinter = POSPrinter()

    override fun printPayment(elderlyPrintModel: ElderlyPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?) {


        val centerArray = arrayOf(0,1,2,29,30,31,34)
        val stringBuilder = StringBuilder()


        stringBuilder.append("${elderlyPrintModel.header}\n\n")
        stringBuilder.append("${elderlyPrintModel.title}\n\n")
        stringBuilder.append("Fecha y hora transaccion:\n")
        stringBuilder.append("${elderlyPrintModel.dateTransaction}\n")
        stringBuilder.append("Numero transaccion: ${elderlyPrintModel.numberTransaction}\n")
        stringBuilder.append("Identificacion beneficiario:\n")
        stringBuilder.append("${elderlyPrintModel.idBeneficiary}\n")
        stringBuilder.append("Nombre y apellido beneficiario:\n")
        stringBuilder.append("${elderlyPrintModel.nameBeneficiary}\n")
        stringBuilder.append("\n")
        stringBuilder.append("Valor: ${elderlyPrintModel.value}\n")
        stringBuilder.append("Municipio: ${elderlyPrintModel.city}\n")
        stringBuilder.append("\n")
        stringBuilder.append("\n")
        stringBuilder.append("                              ---------\n")
        stringBuilder.append("\n")
        stringBuilder.append("\n")
        stringBuilder.append("\n")
        stringBuilder.append("\n")
        stringBuilder.append("\n")
        stringBuilder.append("\n")
        stringBuilder.append("\n")
        stringBuilder.append("-------------------           ---------\n")
        stringBuilder.append("  FIRMA Y CEDULA                HUELLA \n")
        stringBuilder.append("\n")
        stringBuilder.append("\n")
        stringBuilder.append("${elderlyPrintModel.footer}\n")

        stringBuilder.append("OF:${elderlyPrintModel.office} Vendedor:${elderlyPrintModel.sellerCode}\n")
        stringBuilder.append("SV:${elderlyPrintModel.sv} Volumen:${elderlyPrintModel.codeTerminal}\n")
        stringBuilder.append("\n")
        stringBuilder.append("\n")

        if(elderlyPrintModel.isReprint){
            stringBuilder.append("- REIMPRESION -")
        }

        try {
            posPrinter.printWithAlignment(stringBuilder.toString(), centerArray, callback)
        }catch (e: Exception) {
            callback(PrinterStatus.ERROR)
        }



    }


}