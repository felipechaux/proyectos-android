package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestCardRechargeDTO {
    @SerializedName("serie1")
    var series1: String? = null

    @SerializedName("serie2")
    var series2: String? = null

    @SerializedName("canalId")
    var channelId: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("valor")
    var value: Int? = null

    @SerializedName("numeroTarjeta")
    var cardNumber: String? = null

    @SerializedName("tipoOperacion")
    var operationType: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null
}