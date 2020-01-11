package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseRaffleAvailableRangeDTO {

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

    @SerializedName("mensajes")
    var messages: List<MessageDTO>? = null

    @SerializedName("cantidadCifras")
    var quantityFigures: String? = null

    @SerializedName("numeros")
    var numbers: List<String>? = null




}