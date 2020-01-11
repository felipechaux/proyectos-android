package co.com.pagatodo.core.data.model.response

import co.com.pagatodo.core.data.model.PayBillModel

class PayBillResponseModel {
    var responseCode: String? = null
    var isSuccess: Boolean? = null
    var transactionDate: String? = null
    var transactionHour: String? = null
    var message: String? = null
    var payBill: PayBillModel? = null
}