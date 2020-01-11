package co.com.pagatodo.core.data.model.response

import co.com.pagatodo.core.data.model.LotteryDetailModel

class ResponseCheckNumberLotteryModel {
    var responseCode: String? = null
    var isSuccess: Boolean = false
    var transactionDate: String? = null
    var transactionTime: String? = null
    var message: String? = null
    var tickets: List<LotteryNumberModel>? = null
    var numbers: List<LotteryNumberModel>? = null
    var number: LotteryNumberModel? = null
}

class LotteryNumberModel {
    var number: String? = null
    var fraction: String? = null
    var fractions: String? = null
    var serie: String? = null
    var serie1: String? = null
    var serie2: String? = null
    var barcode: String? = null
    var lottery: LotteryDetailModel? = null
}