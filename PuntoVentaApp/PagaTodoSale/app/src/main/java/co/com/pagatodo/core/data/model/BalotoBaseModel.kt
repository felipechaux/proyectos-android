package co.com.pagatodo.core.data.model

import co.com.pagatodo.core.data.dto.MessageDTO

open class BalotoBaseModel {
    var responseCode: String? = null
    var isSuccess: Boolean? = null
    var transactionDate: String? = null
    var transactionId: Int? = null
    var message: String? = null
    var status: String? = null
    var transactionHour: String? = null
    var messages: List<MessageDTO>? = null
}