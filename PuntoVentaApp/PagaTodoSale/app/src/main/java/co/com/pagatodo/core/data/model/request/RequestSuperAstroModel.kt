package co.com.pagatodo.core.data.model.request

import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.SuperAstroModel

class RequestSuperAstroModel {
    var superastroList: List<SuperAstroModel>? = null
    var lotteries: List<LotteryModel>? = null
    var value = 0.0
    var stubs = ""
    var transactionTime: Long? = null
    var sequenceTransaction: Int? = null
}