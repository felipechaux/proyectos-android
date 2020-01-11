package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class BalotoDrawDTO {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("gameName")
    var gameName: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("wagerAvailable")
    var wagerAvailable: Boolean? = null

    @SerializedName("cancelAvailable")
    var cancelAvailable: Boolean? = null

    @SerializedName("closeTime")
    var closeTime: String? = null

    @SerializedName("wagerCloseTime")
    var wagerCloseTime: String? = null

    @SerializedName("cancelCloseTime")
    var cancelCloseTime: String? = null

    @SerializedName("breakEndTime")
    var breakEndTime: String? = null

    @SerializedName("jackpots")
    var jackpots: List<BalotoPotsDTO>? = null
}