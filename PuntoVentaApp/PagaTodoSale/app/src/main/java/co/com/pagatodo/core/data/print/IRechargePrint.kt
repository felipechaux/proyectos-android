package co.com.pagatodo.core.data.print

interface IRechargePrint {
    fun printRecharge(stub: String, billNumber: String, transactionDate: String, transactionHour: String, number:String, value:String, billPrefix: String, billMessage: String, isRetry: Boolean,headerGelsa:String, decriptionPackage:String, operationName :String,callback: (printerStatus: PrinterStatus) -> Unit?)
    fun printBetplay(documentId: String, value: Int, sellerCode: String, digitChecking: String, stubs: String, isRetry: Boolean, typePrintText: String, isReprint: Boolean, isCollect: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
}