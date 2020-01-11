package co.com.pagatodo.core.data.print

import com.pos.device.printer.Printer

class PrinterStatusInfo: IPrinterStatusInfo {
    override fun isOK() = PrintManager.checkPrinterStatus() == Printer.PRINTER_OK
    override fun isBusy() = PrintManager.checkPrinterStatus() == Printer.PRINTER_STATUS_BUSY
    override fun isPaperLack() = PrintManager.checkPrinterStatus() == Printer.PRINTER_STATUS_PAPER_LACK
    override fun getStatus(): PrinterStatus {
        return when(PrintManager.checkPrinterStatus()) {
            Printer.PRINTER_OK -> PrinterStatus.OK
            Printer.PRINTER_STATUS_PAPER_LACK -> PrinterStatus.PAPER_LACK
            Printer.PRINTER_STATUS_BUSY -> PrinterStatus.BUSY
            else -> PrinterStatus.UNKNOWN
        }
    }
}