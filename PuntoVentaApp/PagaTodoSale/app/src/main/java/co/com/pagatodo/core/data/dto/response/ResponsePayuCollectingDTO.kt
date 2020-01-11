package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponsePayuCollectingDTO:BaseResponseDTO(){

    @SerializedName("exitoso")
    var successful: Boolean? = false

    @SerializedName("count")
    var count: Boolean? = false
    @SerializedName("synNromov")
    var synNromov: Int? = null
    @SerializedName("codArriendo")
    var retingCode: String? = null
    @SerializedName("referenciaPago")
    var paymentReference: String? = null
    @SerializedName("valorFactura")
    var billValue: String? = null
    @SerializedName("mensajeRespuesta")
    var responseMessage: String? = null
    @SerializedName("fechaEfectiva")
    var effectiveDate: String? = null
    @SerializedName("codTransaccionPrisma")
    var prismaTransactionCode: String? = null
    @SerializedName("sitioVenta")
    var saleSite: String? = null
    @SerializedName("cantidadMovimientos")
    var transactionQuantity: Int? = null
}