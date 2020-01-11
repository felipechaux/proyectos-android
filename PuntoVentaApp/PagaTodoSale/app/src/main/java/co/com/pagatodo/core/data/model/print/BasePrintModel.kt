package co.com.pagatodo.core.data.model.print

import co.com.pagatodo.core.data.model.LotteryModel

open class BasePrintModel {
    var currentDate: String? = null
    var stubs: String? = "N/A"
    var uniqueSerial: String? = "N/A"
    var digitChecking: String? = "N/A"
    var consultant: String? = null
    var raffleDate: String? = null
    var lotteries: List<LotteryModel>? = null
    var iva: Int? = null
    var totalValueIva: Double? = null
    var totalValueBetting: Double? = null
    var totalValuePaid: Double? = null
    var totalValueOverloaded: Double? = null
    var maxRows:Int? = null
}