package co.com.pagatodo.core.data.model.response

import co.com.pagatodo.core.data.dto.BalotoTaxDTO
import co.com.pagatodo.core.data.dto.ExchangeTicketDTO

class ResponseChargeTicketModel {
    var responseCode: String? = null
    var isSuccess: Boolean? = null
    var message: String? = null
    var transactionDate: String? = null
    var transactionHour: String? = null
    var transactionId: String? = null
    var uniqueSerial: String? = null
    var status: String? = null
    var rejectReason: String? = null
    var serialNumber: String? = null
    var transactionTime: String? = null
    var terminalId: String? = null
    var ticketStatus: String? = null
    var cashAmount: String? = null
    var taxes: List<BalotoTaxDTO>? = null
    var netAmount: String? = null
    var serie1: String? = null
    var serie2: String? = null
    var currentSerie2: String? = null
    var checkDigit: String? = null
    var exchangeTicketDTO : ExchangeTicketDTO? = null
    var wagerGuardNumber :String?= null
}