package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.BalotoResultDTO
import com.google.gson.annotations.SerializedName

class ResponseBalotoResultsDTO {
    @SerializedName("codigoRespuesta")
    var responseCode: String? = null
    @SerializedName("exito")
    var isSuccess = false
    @SerializedName("fechaTransaccion")
    var transactionDate: String? = null
    @SerializedName("horaTransaccion")
    var transactionHour: String? = null
    @SerializedName("mensaje")
    var message: String? = null
    @SerializedName("resultadosBaloto")
    var balotoResults: List<BalotoResultDTO>? = null
}