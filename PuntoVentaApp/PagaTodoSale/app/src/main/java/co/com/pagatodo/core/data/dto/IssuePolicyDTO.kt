package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class IssuePolicyDTO {
    @SerializedName("codigoRespuesta")
    var responseCode: String? = null
    @SerializedName("exito")
    var isSuccess: Boolean? = null
    @SerializedName("fechaTransaccion")
    var transactionDate: String? = null
    @SerializedName("horaTransaccion")
    var transactionHour: String? = null
    @SerializedName("idTransaccionProducto")
    var transactionProductId: String? = null
    @SerializedName("fechaHoraExpedicion")
    var dispatchDateHour: String? = null
    @SerializedName("numMovimiento")
    var transactionQuantity: String? = null
}