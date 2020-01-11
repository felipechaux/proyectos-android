package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class BalotoAddOnGamesDTO {
    @SerializedName("basePrice")
    var basePrice: Int? = null

    @SerializedName("minBoards")
    var minBoards: Int? = null

    @SerializedName("maxBoards")
    var maxBoards: Int? = null

    @SerializedName("quickPickAvailable")
    var quickPickAvailable: Boolean? = null

    @SerializedName("maxDuration")
    var maxDuration: Int? = null

    @SerializedName("primarySelectionsLowNumber")
    var primarySelectionsLowNumber: Int? = null

    @SerializedName("primarySelectionsHighNumber")
    var primarySelectionsHighNumber: Int? = null

    @SerializedName("gameId")
    var gameId: String? = null
}