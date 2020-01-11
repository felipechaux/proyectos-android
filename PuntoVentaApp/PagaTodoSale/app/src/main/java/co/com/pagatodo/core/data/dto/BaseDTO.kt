package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

open class BaseDTO {
    @SerializedName("codigoRespuesta")
    var code: String? = null
    @SerializedName("exito")
    var isSuccess: Boolean = false
}