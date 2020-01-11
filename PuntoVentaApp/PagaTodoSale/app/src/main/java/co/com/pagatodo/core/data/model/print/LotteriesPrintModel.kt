package co.com.pagatodo.core.data.model.print

import co.com.pagatodo.core.data.model.response.LotteryNumberModel

class LotteriesPrintModel {
    var transactionDate: String? = null
    var lotteryName: String? = null
    var draw:String? = null
    var drawDate: String? = null
    var drawHour: String? = null
    var stub: String? = null
    var sellerCode: String? = null
    var digitChecked: String? = null
    var number: String? = null
    var numbers: List<LotteryNumberModel>? = null
    var serie: String? = null
    var prize: String? = null
    var fraction: String? = null
    var value: String? = null
    var fractionValue: String? = null
    var footerText: String? = null
}