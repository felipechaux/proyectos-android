package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class SuperAstroDTO {
    @SerializedName("numero")
    var number: String? = null
    @SerializedName("signo")
    var sign: String? = null
    @SerializedName("valor")
    var value: Double? = null
}