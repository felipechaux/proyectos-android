package co.com.pagatodo.core.data.model

import co.com.pagatodo.core.data.dto.MessageDTO

class ResponseRaffleRegisterModel {
    var responseCode: String? = null
    var isSuccess: Boolean? = null
    var transactionDate: String? = null
    var transactionHour: String? = null
    var transactionId: String? = null
    var message: String? = null
    var messages: List<MessageDTO>? = null
    var uniqueSerial: String? = null
    var serie1: String? = null
    var serie2: String? = null
    var currentSerie2: String? = null
    var raffleDescription: String? = null
}