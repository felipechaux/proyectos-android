package co.com.pagatodo.core.data.model.response

import co.com.pagatodo.core.data.model.BillModel

class PayBillResponseBillModel {
    var responseCode: String? = null
    var isSuccess: Boolean? = null
    var transactionTime: String? = null
    var transactionDate: String? = null
    var message: String? = null
    var bill: BillModel? = null
}