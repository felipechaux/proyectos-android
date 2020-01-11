package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestGeneratePinDTO {
    @SerializedName("usuario")
    var user: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("direccionMac")
    var macAddress: String? = null

    @SerializedName("numDocumento")
    var numberDocument: String? = null

    @SerializedName("canalId")
    var channelId: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("valor")
    var value: String? = null

    @SerializedName("serie1")
    var serie1: String? = null

    @SerializedName("serie2")
    var serie2: String? = null
}