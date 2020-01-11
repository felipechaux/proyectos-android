package co.com.pagatodo.core.data.model.request

import co.com.pagatodo.core.data.model.SportingBetModel

class RequestSellSportingBetModel {
    var value: Int? = null
    var event: SportingEventSellModel? = null
    var product: SportingProductSellModel? = null
    var transactionTime: Long? = null
    var sequenceTransaction: Int? = null
}

class SportingEventSellModel {
    var id: Int? = null
    var bets: List<SportingBetModel>? = null
}

class SportingProductSellModel {
    var id: Int? = null
}