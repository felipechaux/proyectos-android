package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestQueryCardBlackDTO {
    @SerializedName("tipoUsuario")
    var userType: String? = null
    @SerializedName("codigoVendedor")
    var sellerCode: String? = null
    @SerializedName("codigoProducto")
    var productCode: String? = null
    @SerializedName("canalId")
    var channelId: String? = null
    @SerializedName("codigoTerminal")
    var terminalCode: String? = null
    @SerializedName("numeroTarjeta")
    var cardNumber: String? = null
}