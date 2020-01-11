package co.com.pagatodo.core.data.model

import co.com.pagatodo.core.data.dto.BalotoBoardDTO
import co.com.pagatodo.core.data.dto.BalotoTaxDTO
import co.com.pagatodo.core.data.dto.BalotoTicketSecurityDTO

class BalotoSendBetModel: BalotoBaseModel() {
    var transactionTime: String? = null
    var terminalId: Int? = null
    var uniqueSerial: String? = null
    var drawId: String? = null
    var gameName: String? = null
    var serialNumber: String? = null
    var board: List<BalotoBoardDTO>? = null
    var drawStartDate: String? = null
    var drawEndDate: String? = null
    var ticketSecurity: BalotoTicketSecurityDTO? = null
    var tax: List<BalotoTaxDTO>? = null
    var serie1: String? = null
    var serie2: String? = null
    var currentSerie2: String? = null
    var wagerGuardNumber: String? = null
    var checkDigit: String? = null
}