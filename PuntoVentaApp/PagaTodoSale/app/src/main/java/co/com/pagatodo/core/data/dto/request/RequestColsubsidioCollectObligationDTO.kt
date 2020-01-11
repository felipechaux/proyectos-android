package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestColsubsidioCollectObligationDTO {

    @SerializedName("canalId")
    var channelId: String? = null
    @SerializedName("codigoVendedor")
    var sellerCode: String? = null
    @SerializedName("login")
    var login: String? = null
    @SerializedName("codigoTerminal")
    var terminalCode: String? = null
    @SerializedName("tipoUsuario")
    var userType: String? = null
    @SerializedName("direccionMac")
    var macAddress: String? = null
    @SerializedName("codigoProducto")
    var productCode: String? = null
    @SerializedName("recaudoDTO")
    var collection: RequestColsubsidioCollectionDTO? = null
    @SerializedName("clienteDTO")
    var client: ClientCollectObligationDTO? = null

}