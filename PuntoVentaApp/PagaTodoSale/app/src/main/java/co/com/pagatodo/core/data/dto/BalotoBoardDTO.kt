package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class BalotoBoardDTO {
    @SerializedName("quickpick")
    var quickpick: Boolean? = null

    @SerializedName("stake")
    var stake: Int? = null

    @SerializedName("selections")
    var selections: List<String>? = null

    @SerializedName("addonPlayed")
    var addonPlayed: Boolean? = null
}