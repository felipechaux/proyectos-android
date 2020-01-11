package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestRechargeHistoryDTO {
    @SerializedName("numero")
    var number: String? = null
    @SerializedName("fechaConsulta")
    var date: String? = null
    @SerializedName("tipoUsuario")
    var userType: String? = null
    @SerializedName("codigoTerminal")
    var terminalCode: String? = null
    @SerializedName("canalId")
    var canalId: String? = null
    @SerializedName("codigoVendedor")
    var sellerCode: String? = null
    @SerializedName("codigoProducto")
    var productCode: String? = null
}