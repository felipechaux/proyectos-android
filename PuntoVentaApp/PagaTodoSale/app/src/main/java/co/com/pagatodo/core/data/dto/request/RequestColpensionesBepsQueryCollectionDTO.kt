package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestColpensionesBepsQueryCollectionDTO {
    @SerializedName("canalId")
    var channelId: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("numTransaccion")
    var transactionNumber: String? = null

    @SerializedName("tipoConsulta")
    var queryType: String? = null
}