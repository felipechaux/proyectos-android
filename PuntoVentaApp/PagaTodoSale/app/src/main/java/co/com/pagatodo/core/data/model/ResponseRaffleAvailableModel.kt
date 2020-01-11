package co.com.pagatodo.core.data.model

import co.com.pagatodo.core.data.dto.MessageDTO

class ResponseRaffleAvailableModel {
    var responseCode: String? = null
    var isSuccess: Boolean? = null
    var transactionDate: String? = null
    var transactionHour: String? = null
    var message: String? = null
    var messages: List<MessageDTO>? = null
    var availability: String? = null
}