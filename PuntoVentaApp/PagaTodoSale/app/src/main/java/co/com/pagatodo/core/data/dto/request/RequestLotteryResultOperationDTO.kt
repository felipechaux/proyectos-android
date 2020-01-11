package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestLotteryResultOperationDTO {
    @SerializedName("tipoUsuario")
    var userType: String? = null
    @SerializedName("codigoVendedor")
    var sellerCode: String? = null
    @SerializedName("codigoProducto")
    var productCode: String? = null
    @SerializedName("canalId")
    var canalId: String? = null
    @SerializedName("codigoTerminal")
    var terminalCode: String? = null
    @SerializedName("valor")
    var value: String? = null
    @SerializedName("transactionTime")
    var transactionTime: Long? = null
    @SerializedName("secuenciaTransaccion")
    var transactionSequence: Int? = null
    @SerializedName("numero")
    var number: RequestLotteryNumberDTO? = null

    @SerializedName("serie1")
    var serie1: String? = null
    @SerializedName("serie2")
    var serie2: String? = null
    @SerializedName("tipoTransaccionSolicitud")
    var transactionType: String? = null
}