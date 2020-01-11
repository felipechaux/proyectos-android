package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestPinQuickBetDTO {
    @SerializedName("reimpresion")
    var reprint: Boolean? = null

    @SerializedName("direccionMac")
    var macAddress: String? = null

    @SerializedName("volEquipo")
    var teamVol: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null

    @SerializedName("canalId")
    var channelId: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("numDocumento")
    var documentNumber: String? = null
}