package co.com.pagatodo.core.data.model.response

class ResponsePayPhysicalLotteryModel {
    var responseCode: String? = null
    var isSuccess: Boolean = false
    var transactionDate: String? = null
    var transactionDateDrawDate: String? = null
    var transactionTime: String? = null
    var transactionTimeDrawHour: String? = null
    var responseTransactionId: String? = null
    var message: String? = null
    var checkDigit: String? = null
    var serie1: String? = null
    var currentSerie2: String? = null
    var numbers: List<LotteryNumberModel>? = null
}