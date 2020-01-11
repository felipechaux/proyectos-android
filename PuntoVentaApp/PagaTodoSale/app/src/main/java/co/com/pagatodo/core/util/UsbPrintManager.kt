package co.com.pagatodo.core.util.print

import android.content.Context
import android.hardware.usb.UsbManager
import co.com.pagatodo.core.PagaTodoApplication

class UsbPrintManager {

    fun listUsbDevices(): List<UsbPrintManagerInfo>{
        val device = PagaTodoApplication.getAppContext().getSystemService(Context.USB_SERVICE) as UsbManager
        val listDevice = arrayListOf<UsbPrintManagerInfo>()
        device.deviceList.forEach { device ->
            listDevice.add(
                UsbPrintManagerInfo(
                    device.key,
                    device.value.manufacturerName ?: "",
                    "${device.value.manufacturerName} - ${device.value.productName}",
                    "USB:"+device.value.deviceName
                )
            )
        }
        return listDevice
    }
}

data class UsbPrintManagerInfo(
    val key: String,
    val productName: String,
    val productFullName: String = "",
    val productPath: String
) {
    override fun toString(): String {
        return productFullName
    }
}