package co.com.pagatodo.core.data.model.response

import co.com.pagatodo.core.data.dto.MessageDTO

class ResponseSellSportingBetModel {
    var responseCode: String? = null
    var isSuccess: Boolean? = null
    var transactionDate: String? = null
    var transactionHour: String? = null
    var transactionId: String? = null
    var messages: List<MessageDTO>? = null
    var serie1: String? = null
    var consecutive: String? = null
    var currentSerie2: String? = null
    var date: String? = null
    var hour: String? = null
    var verificationCode: String? = null
    var totalValue: Int? = null
    var valueIva:  Int? = null
    var valueNeto: Int? = null
}