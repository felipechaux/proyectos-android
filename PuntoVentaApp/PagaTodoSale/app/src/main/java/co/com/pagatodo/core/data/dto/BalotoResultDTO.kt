package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class BalotoResultDTO {
    @SerializedName("numeroBaloto")
    var balotoNumber: String? = null
    @SerializedName("numeroRevancha")
    var revengeNumber: String? = null
    @SerializedName("sorteo")
    var draw: String? = null
    @SerializedName("fecha")
    var date: String? = null
    @SerializedName("tipo")
    var type: String? = null
    @SerializedName("premios")
    var awards: List<AwarBalotoDTO>? = null
}