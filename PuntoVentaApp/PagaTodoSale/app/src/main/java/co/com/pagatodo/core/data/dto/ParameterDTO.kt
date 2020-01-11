package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class ParameterDTO {
    @SerializedName("clave")
    var key: String? = null
    @SerializedName("valor")
    var value: String? = null
}