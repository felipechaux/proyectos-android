package co.com.pagatodo.core.data.print

enum class PrinterStatus {
    OK, BUSY, HIGHT_TEMP,
    PAPER_LACK, NO_BATTERY, FEED,
    PRINT, FORCE_FEED, POWER_ON,
    UNKNOWN, ERROR
}

interface IPrinterStatusInfo {
    fun isOK(): Boolean
    fun isBusy(): Boolean
    fun isPaperLack(): Boolean
    fun getStatus(): PrinterStatus
}