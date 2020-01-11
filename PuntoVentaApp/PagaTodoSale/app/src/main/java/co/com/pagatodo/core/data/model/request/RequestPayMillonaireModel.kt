package co.com.pagatodo.core.data.model.request

import co.com.pagatodo.core.data.model.ChanceModel
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.ModeValueModel
import co.com.pagatodo.core.data.model.ProductModel

class RequestPayMillonaireModel {
    var chances: List<ChanceModel>? = null
    var lotteries: List<LotteryModel>? = null
    var valueWithoutIva = 0.0
    var product: ProductModel? = null
    var selectedValue: ModeValueModel? = null
    var raffleDate = ""
    var stubs = ""
    var transactionTime: Long? = null
    var sequenceTransaction: Int? = null
}