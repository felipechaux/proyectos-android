package co.com.pagatodo.core.data.model.response

import co.com.pagatodo.core.data.dto.MessageDTO

class PayuResponseModel {
    var responseCode: Boolean? = null
    var isSuccess: Boolean? = null
    var transactionDate: String? = null
    var transactionHour: String? = null
    var messages: List<MessageDTO>? = null
    var count: Boolean? = false
    var payReference: String? = null
    var payerName: String? = null
    var payerDoc: String? = null
    var value: String? = null
    var responseMessage: String? = null
    var transactionQuantity: Int? = null
}