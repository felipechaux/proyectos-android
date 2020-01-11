package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestGetPayuDTO {
    @SerializedName("referenciaPago")
    var paymentReference: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("direccionMac")
    var macAddress: String? = null

    @SerializedName("canalId")
    var channelId: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null
}