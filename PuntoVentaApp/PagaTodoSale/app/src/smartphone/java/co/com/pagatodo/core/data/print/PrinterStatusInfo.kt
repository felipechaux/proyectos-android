package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.print.bluetooth.PrintBluetoothManager

class PrinterStatusInfo: IPrinterStatusInfo {
    override fun isOK() = PrintBluetoothManager.checkPrinterStatus() == 0
    override fun isBusy() = PrintBluetoothManager.checkPrinterStatus() == 1
    override fun isPaperLack() = PrintBluetoothManager.checkPrinterStatus() == 2
    override fun getStatus(): PrinterStatus {
        return when(PrintBluetoothManager.checkPrinterStatus()) {
            0 -> PrinterStatus.OK
            1 -> PrinterStatus.ERROR
            2 -> PrinterStatus.BUSY
            else -> PrinterStatus.UNKNOWN
        }
    }
}