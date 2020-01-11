package co.com.pagatodo.core.data.model.response

import co.com.pagatodo.core.data.model.RechargeHistoryModel

class ResponseRechargeHistoryModel {
    var responseCode: String? = null
    var success: Boolean = false
    var transactionDate: String? = null
    var transactionTime: String? = null
    var message: String = ""
    var recharges: List<RechargeHistoryModel>? = null
}