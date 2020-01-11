package co.com.pagatodo.core.data.print

class PrinterStatusInfo: IPrinterStatusInfo {
    override fun isOK() = false
    override fun isBusy() = false
    override fun isPaperLack() = false
    override fun getStatus(): PrinterStatus {
        return PrinterStatus.OK
    }
}