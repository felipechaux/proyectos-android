package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestPaymillionaireParametersDTO {
    @SerializedName("canalId")
    var canalId: String? = null
    @SerializedName("codigoTerminal")
    var terminalCode: String? = null
    @SerializedName("codigoVendedor")
    var sellerCode: String? = null
    @SerializedName("tipoUsuario")
    var userType: String? = null
    @SerializedName("codigoProducto")
    var productCode: String? = null
    @SerializedName("transactionTime")
    var transactionTime: String? = null
    @SerializedName("secuenciaTransaccion")
    var transactionSequence: Int? = null
}