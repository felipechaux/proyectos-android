package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class ResponseRechargeDTO {
    @SerializedName("codigoRespuesta")
    var responseCode: String? = null
    @SerializedName("exito")
    var success: Boolean? = null
    @SerializedName("idTransaccionRespuesta")
    var transactionAnswerId: Long? = null
    @SerializedName("mensaje")
    var message: String? = null
    @SerializedName("recarga")
    var recharge: RechargeInvoiceDTO? = null
}