package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestQuerySnrGetOfficesDTO {

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
    @SerializedName("volEquipo")
    var kitVol: String? = null
    @SerializedName("tipoUsuario")
    var userType: String? = null

}