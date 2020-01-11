package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestPayBillGetAgreementsDTO {

    @SerializedName("login")
    var login: String? = null

    @SerializedName("canalId")
    var channelId: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("transactionTime")
    var transactionTime: Long? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null
}