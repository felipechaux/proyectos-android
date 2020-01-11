package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

open class BaseResponseDTO {

    @SerializedName("codigoRespuesta")
    var responseCode: String? = null

    @SerializedName("exito")
    var isSuccess: Boolean? = null

    @SerializedName("fechaTransaccion")
    var transactionDate: String? = null

    @SerializedName("horaTransaccion")
    var transactionHour: String? = null

    @SerializedName("idTransaccionRespuesta")
    var transactionId: String? = null

    @SerializedName("mensaje")
    var message: String? = null

    @SerializedName("mensajes")
    var messages: List<MessageDTO>? = null

}