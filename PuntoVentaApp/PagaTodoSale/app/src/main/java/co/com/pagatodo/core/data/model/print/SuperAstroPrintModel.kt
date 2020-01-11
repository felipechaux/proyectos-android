package co.com.pagatodo.core.data.model.print

import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.SuperAstroModel

class SuperAstroPrintModel {
    var textHeader: String? = null
    var drawDate: String? = null
    var stub: String? = null
    var sellerCode: String? = null
    var lotteries: List<LotteryModel>? = null
    var pdvCode: String? = null
    var checkNumber: String? = null
    var digitChecking: String? = null
    var superastroList: List<SuperAstroModel>? = null
    var ticketPrizePlan: String? = null
    var totalValueBetting: Double? = null
    var totalValuePaid: Double? = null
    var totalValueIva: Double? = null
    var draw: String? = null
    var ticketFooter: String? = null
}