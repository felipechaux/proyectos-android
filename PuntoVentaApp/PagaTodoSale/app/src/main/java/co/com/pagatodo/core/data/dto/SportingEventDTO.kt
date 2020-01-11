package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName



class SportingEventDTO {
    @SerializedName("idEvento")
    var eventId: Int? = null

    @SerializedName("codigo")
    var code: String? = null

    @SerializedName("nombre")
    var name: String? = null

    @SerializedName("valorApuesta")
    var betValue: Int? = null

    @SerializedName("valorMultiple")
    var multipleVale: String? = null

    @SerializedName("fechaCierre")
    var closeDate: String? = null

    @SerializedName("horaCierre")
    var closeTime: String? = null

    @SerializedName("lstApuestas")
    var bets: List<SportingBetDTO>? = null

    @SerializedName("acumulado")
    var accumulated: Int? = null
}