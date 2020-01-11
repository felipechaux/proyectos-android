package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class ResponseAlcanosDTO {
    @SerializedName("codigo")
    var code: String? = null

    @SerializedName("mensaje")
    var message: String? = null

    @SerializedName("exito")
    var isSuccess: Boolean? = null
}