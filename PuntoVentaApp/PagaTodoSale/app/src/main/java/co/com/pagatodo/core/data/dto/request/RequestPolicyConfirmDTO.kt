package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestPolicyConfirmDTO {

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("direccionMac")
    var macAddress: String? = null

    @SerializedName("canalId")
    var channelId: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("estado")
    var state: String? = null

    @SerializedName("valorRecaudo")
    var collectionValue: String? = null

    @SerializedName("placa")
    var plaque: String? = null

    @SerializedName("codEntidad")
    var entityCode: String? = null

    @SerializedName("idTransaccionProducto")
    var productTransactionId: String? = null
}