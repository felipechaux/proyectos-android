package co.com.pagatodo.core.data.model.response

import co.com.pagatodo.core.data.dto.MessageDTO

class PayuCollectingResponseModel {
    var responseCode: String? = null
    var isSuccess: Boolean? = null
    var transactionDate: String? = null
    var transactionHour: String? = null
    var successful: Boolean? = false
    var messages: List<MessageDTO>? = null
    var count: Boolean? = false
    var synNromov: Int? = null
    var retingCode: String? = null
    var paymentReference: String? = null
    var billValue: String? = null
    var responseMessage: String? = null
    var effectiveDate: String? = null
    var prismaTransactionCode: String? = null
    var saleSite: String? = null
    var transactionQuantity: Int? = null
}