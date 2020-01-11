package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class RaffleDTO {
    @SerializedName("idRifa")
    var raffleId: Int? = null

    @SerializedName("nombreRifa")
    var raffleName: String? = null

    @SerializedName("descripcionRifa")
    var descriptionRaffle: String? = null

    @SerializedName("nombreLoteria")
    var lotteryName: String? = null

    @SerializedName("fechaSorteo")
    var dateDraw: String? = null

    @SerializedName("valor")
    var price: Int? = null

    @SerializedName("cantidadCifras")
    var countNumberFigures: Int? = null

    @SerializedName("valorPremio")
    var prizeValue: String? = null

    @SerializedName("horaSorteo")
    var drawTime: String? = null

    @SerializedName("logoImpresion")
    var logo: Int? = null

    @SerializedName("mensajeDescripcionRifa")
    var messageRaffle: String? = null
}