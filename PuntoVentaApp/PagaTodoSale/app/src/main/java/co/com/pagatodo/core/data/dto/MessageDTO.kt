package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class MessageDTO {
    @SerializedName("codigo")
    var code: String? = null

    @SerializedName("tipoMensaje")
    var messageType: String? = null

    @SerializedName("mensaje")
    var message: String? = null
}