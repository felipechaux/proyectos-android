package co.com.pagatodo.core.data.print

import co.com.pagatodo.core.data.model.print.BalotoPrintModel
import co.com.pagatodo.core.util.DatafonoType
import co.com.pagatodo.core.util.DeviceUtil

class BalotoPrint : BasePrint(), IBalotoPrint {

    override fun printBalotoSaleTicket(
        balotoPrintModel: BalotoPrintModel,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        when (DeviceUtil.getDeviceModel()) {

            DatafonoType.Q2.type -> BalotoPrintQ2().printBalotoSaleTicket(balotoPrintModel, callback)
            DatafonoType.NEW9220.type -> BalotoPrintNEW9220().printBalotoSaleTicket(balotoPrintModel, callback)
        }


    }

    fun printBalotoTransactionTicket(printModel: BalotoPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?) {

        when (DeviceUtil.getDeviceModel()) {

            DatafonoType.Q2.type -> BalotoPrintQ2().printBalotoTransactionTicket(printModel, callback)
            DatafonoType.NEW9220.type -> BalotoPrintNEW9220().printBalotoTransactionTicket(printModel, callback)
        }

    }

    override fun printBalotoQueryTicket(
        transactionDate: String,
        numberTicket: String,
        valuePrize: String,
        tax: String,
        valuePay: String,
        terminalCode: String,
        salePointCode: String,
        authorizerNumber: String,
        stub: String,
        sellerCode: String,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        when (DeviceUtil.getDeviceModel()) {

            DatafonoType.Q2.type -> BalotoPrintQ2().printBalotoQueryTicket(
                transactionDate,
                numberTicket,
                valuePrize,
                tax,
                valuePay,
                terminalCode,
                salePointCode,
                authorizerNumber,
                stub,
                sellerCode,
                callback
            )
            DatafonoType.NEW9220.type -> BalotoPrintNEW9220().printBalotoQueryTicket(
                transactionDate,
                numberTicket,
                valuePrize,
                tax,
                valuePay,
                terminalCode,
                salePointCode,
                authorizerNumber,
                stub,
                sellerCode,
                callback
            )
        }

    }


}