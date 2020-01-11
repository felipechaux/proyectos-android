package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class ResponseBalotoCheckTicketDTO :BaseResponseDTO() {


    @SerializedName("status")
    var status: String? = null
    @SerializedName("canPay")
    var canPay: Boolean? = null
    @SerializedName("ticketStatus")
    var ticketStatus: String? = null
    @SerializedName("cashAmount")
    var cashAmount: String? = null
    @SerializedName("netAmount")
    var netAmount: String? = null
    @SerializedName("serialNumber")
    var serialNumber: String? = null
    @SerializedName("rejectReason")
    var rejectReason: String? = null
    @SerializedName("transactionTime")
    var transactionTime: Long? = null
}