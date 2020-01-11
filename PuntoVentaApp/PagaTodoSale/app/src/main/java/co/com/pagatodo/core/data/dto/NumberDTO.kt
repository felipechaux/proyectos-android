package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class NumberDTO {
    @SerializedName("loteria")
    var lottery: LotteryDTO? = null
    @SerializedName("numero")
    var number: String? = null
    @SerializedName("fraccion")
    var fraction: String? = null
    @SerializedName("serie")
    var serie: String? = null
    @SerializedName("codigoBarras")
    var barCode: String? = null
    @SerializedName("serie1")
    var serie1: String? = null
    @SerializedName("serie2")
    var serie2: String? = null
}