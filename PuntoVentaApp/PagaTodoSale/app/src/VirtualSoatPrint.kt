package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.print.VirtualSoatPrintModel
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.print.POSPrinter
import java.lang.Exception

class VirtualSoatPrint: BasePrint(), IVirtualSoatPrint {
    private val stringBuilder = StringBuilder()
    private var posPrinter = POSPrinter()

    override fun print(printModel: VirtualSoatPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?) {
        stringBuilder.clear()

        val header = printModel.header?.split("\\n") ?: arrayListOf()
        stringBuilder.append("\n${header[0]}")
        stringBuilder.append("\n${header[1]}")

        stringBuilder.append("\n\nRECAUDO DE POLIZA SOAT")
        stringBuilder.append("\nFecha Hora de Expedición:")
        val date = DateUtil.convertStringToDate(DateUtil.StringFormat.EEE_DD_MM_YYYY, DateUtil.addBackslashToStringDate(printModel.transactionDate ?: ""))
        stringBuilder.append("\n$date ${printModel.transactionHour}")
        stringBuilder.append("\nFecha Hora de Inicio Vigencia:\n${printModel.beginValidityDateHour}")
        stringBuilder.append("\nFecha Hora de Fin Vigencia:\n${printModel.endValidityDateHour}")
        stringBuilder.append("\nNo Poliza: ${printModel.policyNumber}")
        stringBuilder.append("\nPrima SOAT:\n$${printModel.soatValue}")
        stringBuilder.append("\nContribucion Fosyga:\n$${printModel.fosygaValue}")
        stringBuilder.append("\nTarifa RUNT:\n$${printModel.runtValue}")
        stringBuilder.append("\nTotal a pagar:\n$${printModel.totalValue}")
        stringBuilder.append("\n------")
        stringBuilder.append("\nINFORMACION DEL TOMADOR")
        stringBuilder.append("\nNombres y Apellidos del Tomador:\n${printModel.takerNameLast}")
        stringBuilder.append("\nTipo Documento:\n${printModel.documentType}")
        stringBuilder.append("\nNum. Documento:\n${printModel.takerDocumentNumber}")
        stringBuilder.append("\nCiudad Residencia Tomador:\n${printModel.takerCity}")
        stringBuilder.append("\nINFORMACION DEL VEHICULO")
        stringBuilder.append("\nClase Vehiculo: ${printModel.vehicleType}")
        stringBuilder.append("\nServicio: ${printModel.service}")
        stringBuilder.append("\nCilindraje: ${printModel.cylinderCapacity}")
        stringBuilder.append("\nModelo: ${printModel.vehicleModel}")
        stringBuilder.append("\nPlaca: ${printModel.licensePlate}")
        stringBuilder.append("\nMarca: ")
        stringBuilder.append("\nLinea: ${printModel.vehicleLine}")
        stringBuilder.append("\nNo Motor: ${printModel.motorNumber}")
        stringBuilder.append("\nNo Chasis: ${printModel.chassisNumber}")
        stringBuilder.append("\nNo VIN: ${printModel.vinNumber}")
        stringBuilder.append("\nPasajeros: ${printModel.passengers}")
        stringBuilder.append("\nCapacidad: ${printModel.capacity}")
        stringBuilder.append("\nINFORMACION ADICIONAL")
        stringBuilder.append("\nNombre del punto del Recaudador: ------")
        stringBuilder.append("\nCodigo del punto del Recaudador: ------")
        stringBuilder.append("\nCiudad: ------")
        stringBuilder.append("\nMovimiento: ${printModel.transactionQuantity}")
        stringBuilder.append("\n------")

        try {
            posPrinter.printWithAlignment(stringBuilder.toString(), arrayOf(1,2), callback)
        }catch (e: Exception) {
            callback(PrinterStatus.ERROR)
        }
    }

}