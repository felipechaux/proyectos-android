package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class StubDTO {

    @SerializedName("serie1")
    var serie1: String? = null
    @SerializedName("serie2")
    var serie2: String? = null
    @SerializedName("serialUnico")
    var uniqueSerie: String? = null
    @SerializedName("enUso")
    var inUse: Boolean = false
    @SerializedName("codEntidad")
    var entityCode: String? = null
    @SerializedName("rangoFinal")
    var finalRank: String? = null
    @SerializedName("tipoChance")
    var chanceType: String? = null
}