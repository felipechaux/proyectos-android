package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestPayuReprintDTO {
    @SerializedName("tipoUsuario")
    var userType: String? = null
    @SerializedName("direccionMac")
    var mac: String? = null
    @SerializedName("canalId")
    var channelId: String? = null
    @SerializedName("codigoTerminal")
    var terminalCode: String? = null
    @SerializedName("codigoVendedor")
    var sellerCode: String? = null
    @SerializedName("codigoProducto")
    var productCode: String? = null
    @SerializedName("synNromov")
    var synNromov: String? = null

    @SerializedName("referenciaPago")
    var payReference: String? = null
}