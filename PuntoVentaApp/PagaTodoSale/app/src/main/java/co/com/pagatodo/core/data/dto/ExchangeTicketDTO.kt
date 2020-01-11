package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class ExchangeTicketDTO {

    @SerializedName("boards")
    var board: List<BalotoBoardDTO>? = null

    @SerializedName("drawStartDate")
    var drawStartDate: String? = null

    @SerializedName("drawEndDate")
    var drawEndDate: String? = null

    @SerializedName("drawId")
    var drawId: String? = null

    @SerializedName("duration")
    var duration: Int? = null

    @SerializedName("gameName")
    var gameName: String? = null

    @SerializedName("price")
    var price: Int? = null

    @SerializedName("taxes")
    var tax: List<BalotoTaxDTO>? = null

    @SerializedName("transactionTime")
    var transactionTime: String? = null


}