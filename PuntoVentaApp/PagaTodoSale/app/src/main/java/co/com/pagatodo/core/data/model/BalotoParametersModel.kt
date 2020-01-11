package co.com.pagatodo.core.data.model

import co.com.pagatodo.core.data.dto.BalotoAddOnGamesDTO
import co.com.pagatodo.core.data.dto.BalotoDrawDTO

class BalotoParametersModel: BalotoBaseModel() {
    var basePrice: Int? = null
    var maxPrice: Int? = null
    var minBoards: Int? = null
    var maxBoards: Int? = null
    var quickPickAvailable: Boolean? = null
    var multiplierAvailable: Boolean? = null
    var stakes: List<Int>? = null
    var durations: List<Int>? = null
    var maxDuration: Int? = null
    var primarySelectionsLowNumber: Int? = null
    var primarySelectionsHighNumber: Int? = null
    var secondarySelectionsLowNumber: Int? = null
    var secondarySelectionsHighNumber: Int? = null
    var gameId: String? = null
    var revision: String? = null
    var addonGames: List<BalotoAddOnGamesDTO>? = null
    var draws: List<BalotoDrawDTO>? = null
    var productParameters: ProductParametersModel? = null
    var isLoad: Boolean = false
}

class ProductParametersModel {
    var headerTicketSp: String? = null
    var footerTicketSp: String? = null
    var headerTicketPv: String? = null
    var footerTicketPv: String? = null
}