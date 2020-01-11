package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestCheckServicesInvoiceDTO {
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
}