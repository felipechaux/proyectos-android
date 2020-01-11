package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseGetPayuDTO {
    @SerializedName("codigoRespuesta")
    var responseCode: Boolean? = false
    @SerializedName("exito")
    var isSuccess: Boolean? = null
    @SerializedName("fechaTransaccion")
    var transactionDate: String? = null
    @SerializedName("horaTransaccion")
    var transactionHour: String? = null
    @SerializedName("mensajes")
    var messages: List<MessageDTO>? = null
    @SerializedName("count")
    var count: Boolean? = false
    @SerializedName("referenciaPago")
    var payReference: String? = null
    @SerializedName("nombrePagador")
    var payerName: String? = null
    @SerializedName("documentoPagador")
    var payerDoc: String? = null
    @SerializedName("valorFactura")
    var value: String? = null
    @SerializedName("mensajeRespuesta")
    var responseMessage: String? = null
    @SerializedName("cantidadMovimientos")
    var transactionQuantity: Int? = null
}