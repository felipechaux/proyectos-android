package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class ChanceDTO {
    @SerializedName("numero")
    var number: String? = null
    @SerializedName("plenoDirecto")
    var direct: Int? = null
    @SerializedName("plenoCombinado")
    var combined: Int? = null
    @SerializedName("superPlenoDirecto")
    var superDirect: Int? = null
    @SerializedName("superPlenoCombinado")
    var superCombined: Int? = null
    @SerializedName("pata")
    var paw: Int? = null
    @SerializedName("una")
    var nail: Int? = null

}