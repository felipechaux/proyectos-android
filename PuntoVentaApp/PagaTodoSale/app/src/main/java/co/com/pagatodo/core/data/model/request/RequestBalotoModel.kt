package co.com.pagatodo.core.data.model.request

import co.com.pagatodo.core.data.dto.BalotoBoardDTO
import co.com.pagatodo.core.data.model.BalotoBoardModel

class RequestBalotoModel {
    var headerBaloto: String? = null
    var transactionDate: String? = null
    var boardList: List<BalotoBoardModel>? = null
    var totalValueWithoutRematch = ""
    var totalValueRematch = ""
    var iva = 0
    var totalWithoutIva = 0
    var total = ""
    var drawInfo = 0
    var serialNumber: String? = null
    var checkDigit: String? = null
    var wagerGuardNumber: String? = null
    var footerTicket: String? = null
    var duration = 0
    var transactionTime: Long? = null
    var sequenceTransaction: Int? = null
}