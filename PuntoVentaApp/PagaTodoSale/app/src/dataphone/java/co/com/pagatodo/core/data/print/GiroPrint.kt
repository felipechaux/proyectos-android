package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.dto.GirorCheckRequestsDTO
import co.com.pagatodo.core.util.DatafonoType
import co.com.pagatodo.core.util.DeviceUtil


class GiroPrint : IGiroPrint {

    override fun printPaymentGiro(
        data: List<String>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        when (DeviceUtil.getDeviceModel()) {
            DatafonoType.Q2.type -> GiroPrintQ2().printPaymentGiro(data, isRetry, callback)
            DatafonoType.NEW9220.type -> GiroPrintNEW9220().printPaymentGiro(
                data,
                isRetry,
                callback
            )
        }

    }

    override fun printCaptureGiro(
        data: List<String>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        when (DeviceUtil.getDeviceModel()) {
            DatafonoType.Q2.type -> GiroPrintQ2().printCaptureGiro(data, isRetry, callback)
            DatafonoType.NEW9220.type -> GiroPrintNEW9220().printCaptureGiro(
                data,
                isRetry,
                callback
            )
        }

    }

    override fun printCheckRequest(
        girorCheckRequestsDTO: GirorCheckRequestsDTO,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {


        when (DeviceUtil.getDeviceModel()) {
            DatafonoType.Q2.type -> GiroPrintQ2().printCheckRequest(
                girorCheckRequestsDTO,
                isRetry,
                callback
            )
            DatafonoType.NEW9220.type -> GiroPrintNEW9220().printCheckRequest(
                girorCheckRequestsDTO,
                isRetry,
                callback
            )
        }
    }

    override fun printCloseBox(
        data: List<String>,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        when (DeviceUtil.getDeviceModel()) {
            DatafonoType.Q2.type -> GiroPrintQ2().printCloseBox(data, isRetry, callback)
            DatafonoType.NEW9220.type -> GiroPrintNEW9220().printCloseBox(data, isRetry, callback)
        }
    }

}