package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestPayBillRePrintDTO {
    @SerializedName("login")
    var login: String? = null

    @SerializedName("numeroCuenta")
    var accountNumber: String? = null

    @SerializedName("fechaRecaudo")
    var collectionDate: String? = null

    @SerializedName("canalId")
    var channelId: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("volEquipo")
    var teamVol: String? = null

    @SerializedName("direccionMac")
    var macAddress: String? = null
}