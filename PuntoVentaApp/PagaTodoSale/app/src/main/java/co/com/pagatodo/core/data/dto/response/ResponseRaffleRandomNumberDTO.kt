package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class ResponseRaffleRandomNumberDTO {
    @SerializedName("codigoRespuesta")
    var code: String? = null
    @SerializedName("exito")
    var isSuccess: Boolean = false
    @SerializedName("fechaTransaccion")
    var transactionDate: String? = null
    @SerializedName("horaTransaccion")
    var hourTransaction: String? = null
    @SerializedName("mensaje")
    var message: String? = null
    @SerializedName("numero")
    var number: String? = null
}