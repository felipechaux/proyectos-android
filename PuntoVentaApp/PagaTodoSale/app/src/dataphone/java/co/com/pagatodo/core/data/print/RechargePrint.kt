package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.util.DatafonoType
import co.com.pagatodo.core.util.DeviceUtil

class RechargePrint : BasePrint(), IRechargePrint {

    override fun printRecharge(
        stub: String,
        billNumber: String,
        transactionDate: String,
        transactionHour: String,
        number: String,
        value: String,
        billPrefix: String,
        billMessage: String,
        isRetry: Boolean,
        headerGelsa:String,
        decriptionPackage:String,
        operationName :String,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        when (DeviceUtil.getDeviceModel()) {

            DatafonoType.Q2.type -> RechargePrintQ2().printRecharge(stub ,billNumber ,transactionDate ,transactionHour ,number ,value ,billPrefix ,billMessage ,isRetry ,headerGelsa, decriptionPackage,operationName, callback)
            DatafonoType.NEW9220.type -> RechargePrintNEW9220().printRecharge(stub ,billNumber ,transactionDate ,transactionHour ,number ,value ,billPrefix ,billMessage ,isRetry,headerGelsa, decriptionPackage,operationName, callback)
        }

    }

    override fun printBetplay(
        documentId: String,
        value: Int,
        sellerCode: String,
        digitChecking: String,
        stubs: String,
        isRetry: Boolean,
        typePrintText: String,
        isReprint: Boolean,
        isCollect: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        when (DeviceUtil.getDeviceModel()) {

            DatafonoType.Q2.type -> RechargePrintQ2().printBetplay(documentId ,value ,sellerCode ,digitChecking ,stubs ,isRetry ,typePrintText ,isReprint ,isCollect ,callback)
            DatafonoType.NEW9220.type -> RechargePrintNEW9220().printBetplay(documentId ,value ,sellerCode ,digitChecking ,stubs ,isRetry ,typePrintText ,isReprint ,isCollect ,callback)
        }

    }





}