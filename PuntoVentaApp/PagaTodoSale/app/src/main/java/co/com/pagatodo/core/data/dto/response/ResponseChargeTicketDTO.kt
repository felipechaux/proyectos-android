package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.BalotoTaxDTO
import co.com.pagatodo.core.data.dto.BalotoTicketSecurityDTO
import co.com.pagatodo.core.data.dto.ExchangeTicketDTO
import com.google.gson.annotations.SerializedName

class ResponseChargeTicketDTO :BaseResponseDTO() {

    @SerializedName("serialUnico")
    var uniqueSerial: String? = null
    @SerializedName("status")
    var status: String? = null
    @SerializedName("rejectReason")
    var rejectReason: String? = null
    @SerializedName("serialNumber")
    var serialNumber: String? = null
    @SerializedName("transactionTime")
    var transactionTime: String? = null

    @SerializedName("idTerminal")
    var terminalId: String? = null
    @SerializedName("ticketStatus")
    var ticketStatus: String? = null
    @SerializedName("cashAmount")
    var cashAmount: String? = null
    @SerializedName("taxes")
    var taxes: List<BalotoTaxDTO>? = null
    @SerializedName("netAmount")
    var netAmount: String? = null
    @SerializedName("serie1")
    var serie1: String? = null
    @SerializedName("serie2")
    var serie2: String? = null
    @SerializedName("serie2Actual")
    var currentSerie2: String? = null
    @SerializedName("digitoChequeo")
    var checkDigit: String? = null

    @SerializedName("wagerGuardNumber")
    var wagerGuardNumber: String? = null

    @SerializedName("exchangeTicket")
    var exchangeTicket: ExchangeTicketDTO? = null

    @SerializedName("ticketSecurity")
    var ticketSecurity: BalotoTicketSecurityDTO? = null

}