package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class PinDTO {
    @SerializedName("pin")
    var pin: String? = null

    @SerializedName("valor")
    var value: String? = null

    @SerializedName("tipoPin")
    var pinType: String? = null
}