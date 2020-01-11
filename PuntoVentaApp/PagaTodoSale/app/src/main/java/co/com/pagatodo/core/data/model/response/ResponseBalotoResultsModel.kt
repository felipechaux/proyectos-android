package co.com.pagatodo.core.data.model.response

import co.com.pagatodo.core.data.model.BalotoResultsModel

class ResponseBalotoResultsModel {
    var responseCode: String? = null
    var isSuccess = false
    var transactionDate: String? = null
    var transactionHour: String? = null
    var message: String? = null
    var balotoResults: List<BalotoResultsModel>? = null
}