package co.com.pagatodo.core.views.deviceprinters

import androidx.lifecycle.ViewModel
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil
import co.com.pagatodo.core.util.print.UsbPrintManager
import co.com.pagatodo.core.util.print.UsbPrintManagerInfo
import co.com.pagatodo.core.views.SingleLiveEvent

class DevicePrintersViewModel: ViewModel() {

    sealed class ViewEvent {
        class LoadUsbDevices(val listUbsDevices: List<UsbPrintManagerInfo>): ViewEvent()
    }

    var singleLiveEvent: SingleLiveEvent<ViewEvent> = SingleLiveEvent()

    fun loadUsbDevices() {
        val internalPrintName = R_string(R.string.text_select_internal_print)
        val usbManager = UsbPrintManager()
        val list = usbManager.listUsbDevices().toMutableList()
        list.add(0, UsbPrintManagerInfo("", "", R_string(R.string.text_label_select_printer), ""))
        list.add(UsbPrintManagerInfo(R_string(R.string.usb_print_key), internalPrintName, internalPrintName.toUpperCase(), ""))
        singleLiveEvent.postValue(ViewEvent.LoadUsbDevices(list))
    }

    fun validateJsaPrinter(): PrinterStatus {
        val jsaPrinter = SharedPreferencesUtil.getPreference<String>(R_string(R.string.shared_key_jsa_printer))
        val usbManager = UsbPrintManager()
        val list = usbManager.listUsbDevices().toMutableList()
        list.forEach {
            if (it.productPath == jsaPrinter){
                return PrinterStatus.OK
            }
        }

        return PrinterStatus.ERROR
    }

}