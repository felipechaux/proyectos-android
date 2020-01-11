package co.com.pagatodo.core.data.model.request

import co.com.pagatodo.core.data.model.*
import java.util.*

class RequestSuperChanceModel {
    var chances: List<SuperchanceModel>? = null
    var lotteries: List<LotteryModel>? = null
    var totalValue = 0.0
    var raffleDateModel: Date? = null
    var stubs = ""
    var transactionTime: Long? = null
    var sequenceTransaction: Int? = null
    var maxRows: Int? = null

}