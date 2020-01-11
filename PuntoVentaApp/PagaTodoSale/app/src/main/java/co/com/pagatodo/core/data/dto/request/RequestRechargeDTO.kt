package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.RechargeDTO
import com.google.gson.annotations.SerializedName

class RequestRechargeDTO {

    @SerializedName("canalId")
    var canalId: String? = null
    @SerializedName("codigoTerminal")
    var terminalCode: String? = null
    @SerializedName("codigoVendedor")
    var sellerCode: String? = null
    @SerializedName("tipoUsuario")
    var userType: String? = null
    @SerializedName("recarga")
    var rechargeDTO: RechargeDTO? = null
    @SerializedName("valor")
    var value: String? = null
    @SerializedName("codigoProducto")
    var productCode: String? = null
    @SerializedName("serie1")
    var serie1: String? = null
    @SerializedName("serie2")
    var serie2: String? = null
    @SerializedName("transactionTime")
    var transactionTime: Long? = null
    @SerializedName("secuenciaTransaccion")
    var transactionSequence: Int? = null
    @SerializedName("tipoTransaccionSolicitud")
    var transactionType: String? = null
}