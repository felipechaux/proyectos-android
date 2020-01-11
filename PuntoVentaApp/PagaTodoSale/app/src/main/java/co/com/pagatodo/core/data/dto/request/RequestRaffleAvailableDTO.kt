package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestRaffleAvailableDTO {

    @SerializedName("canalId")
    var channelId: String? = null

    @SerializedName("codigoRifa")
    var raffleCode: String? = null

    @SerializedName("numero")
    var number: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null

}