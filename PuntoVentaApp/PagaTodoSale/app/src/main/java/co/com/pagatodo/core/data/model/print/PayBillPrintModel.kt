package co.com.pagatodo.core.data.model.print

import co.com.pagatodo.core.data.model.BillModel

class PayBillPrintModel {
    var date: String? = null
    var hour: String? = null
    var voucherNumber: String? = null
    var billNumber: String? = null
    var referenceCode: String? = null
    var code: String? = null
    var descriptionService: String? = null
    var header: String? = null
    var valueToPay: Double? = null
    var totalValue: Double? = null
    var bill: BillModel? = null
}