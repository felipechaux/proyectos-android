package co.com.pagatodo.core.data.model.response

import co.com.pagatodo.core.data.dto.MessageDTO
import co.com.pagatodo.core.data.dto.TransactionsDTO

class PayuResponseReprintModel {
    var responseCode: String? = null
    var isSuccess: Boolean? = null
    var transactionDate: String? = null
    var transactionHour: String? = null
    var messages: List<MessageDTO>? = null
    var count: Boolean? = false
    var transactionQuantity: String? = null
    var pageFoot: String? = null
    var transactions: List<TransactionsDTO>? = null
}