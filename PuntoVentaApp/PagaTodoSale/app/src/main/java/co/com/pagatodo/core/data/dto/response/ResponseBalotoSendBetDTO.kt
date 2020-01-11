package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.BalotoBoardDTO
import co.com.pagatodo.core.data.dto.BalotoTaxDTO
import co.com.pagatodo.core.data.dto.BalotoTicketSecurityDTO
import com.google.gson.annotations.SerializedName

class ResponseBalotoSendBetDTO :BaseResponseDTO() {


    @SerializedName("idTerminal")
    var terminalId: Int? = null

    @SerializedName("serialUnico")
    var uniqueSerial: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("drawId")
    var drawId: String? = null

    @SerializedName("gameName")
    var gameName: String? = null

    @SerializedName("serialNumber")
    var serialNumber: String? = null

    @SerializedName("boards")
    var board: List<BalotoBoardDTO>? = null

    @SerializedName("transactionTime")
    var transactionTime: String? = null

    @SerializedName("drawStartDate")
    var drawStartDate: String? = null

    @SerializedName("drawEndDate")
    var drawEndDate: String? = null

    @SerializedName("ticketSecurityDTO")
    var ticketSecurity: BalotoTicketSecurityDTO? = null

    @SerializedName("taxes")
    var tax: List<BalotoTaxDTO>? = null

    @SerializedName("serie1")
    var serie1: String? = null

    @SerializedName("serie2")
    var serie2: String? = null

    @SerializedName("serie2Actual")
    var currentSerie2: String? = null

    @SerializedName("wagerGuardNumber")
    var wagerGuardNumber: String? = null

    @SerializedName("digitoChequeo")
    var checkDigit: String? = null
}