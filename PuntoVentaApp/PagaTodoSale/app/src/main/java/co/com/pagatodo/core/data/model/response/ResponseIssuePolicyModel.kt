package co.com.pagatodo.core.data.model.response

import co.com.pagatodo.core.data.model.IssuePolicyModel

class ResponseIssuePolicyModel {
    var responseCode: String? = null
    var isSuccess: Boolean? = null
    var transactionDate: String? = null
    var transactionHour: String? = null
    var responseTransactionId: String? = null
    var message: String? = null
    var issuePolicy: IssuePolicyModel? = null
}