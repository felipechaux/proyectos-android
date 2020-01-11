package co.com.pagatodo.core.data.model.response

import co.com.pagatodo.core.data.model.SportingProductModel

class ResponseSportingModel {
    var serie1: String? = null
    var serie2: String? = null
    var idExternalProduct: Int? = null
    var resolution: String? = null
    var productName: String? = null
    var version: Int? = null
    var products: List<SportingProductModel>? = null
    var prizePlan: Int? = null
    var megaGoalMaxBet: Int? = null
}