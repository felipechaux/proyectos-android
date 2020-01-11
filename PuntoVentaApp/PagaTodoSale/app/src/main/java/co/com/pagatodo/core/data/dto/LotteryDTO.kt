package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class LotteryDTO {

    @SerializedName("codLoteria")
    var code: String? = null
    @SerializedName("nombreCorto")
    var name: String? = null
    @SerializedName("nombreCompletoLoteria")
    var fullName: String? = null
    @SerializedName("fecha")
    var date: String? = null
    @SerializedName("hora")
    var hour: String? = null
    @SerializedName("loteriaDia")
    var lotteryDay: String? = null
    @SerializedName("cifrasNumero")
    var numberFigures: Int = 0
    @SerializedName("cifrasSerie")
    var serieFigure: Int = 0

    @SerializedName("fracciones")
    var fractions: String? = null
    @SerializedName("valorFraccion")
    var fractionValue: String? = null
    @SerializedName("sorteo")
    var draw: String? = null
    @SerializedName("premio")
    var award: String? = null
}