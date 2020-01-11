package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class RandomNumberDTO {

    @SerializedName("codigoRespuesta")
    var code: String? = null
    @SerializedName("exito")
    var isSuccess: Boolean = false

    @SerializedName("numerosGenerados")
    var numbers: List<String>? = null
    @SerializedName("fechaTransaccion")
    var transactionDate: String? = null
    @SerializedName("horaTransaccion")
    var hourTransaction: String? = null
    @SerializedName("mensaje")
    var message: String? = null
}