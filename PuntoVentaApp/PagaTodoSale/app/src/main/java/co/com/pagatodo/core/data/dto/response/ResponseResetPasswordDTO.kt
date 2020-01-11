package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class ResponseResetPasswordDTO {
    @SerializedName("codigoRespuesta")
    var responseCode: String? = null
    @SerializedName("exito")
    var success: Boolean = false
    @SerializedName("fechaTransaccion")
    var transactionDate: String? = null
    @SerializedName("horaTransaccion")
    var transactionTime: String? = null
    @SerializedName("mensaje")
    var message: String = ""
}