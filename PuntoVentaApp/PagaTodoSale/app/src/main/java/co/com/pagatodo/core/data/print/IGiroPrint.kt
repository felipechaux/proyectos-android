package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.dto.GirorCheckRequestsDTO

interface IGiroPrint {

    fun printCaptureGiro(data:List<String>, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
    fun printPaymentGiro(data:List<String>, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
    fun printCheckRequest(girorCheckRequestsDTO: GirorCheckRequestsDTO, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
    fun printCloseBox(data:List<String>, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
}