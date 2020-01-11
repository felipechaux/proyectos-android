package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class RequestRegistraduriaGetCollectionDTO {

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
    var deviceVol: String? = null
    @SerializedName("tipoUsuario")
    var userType: String? = null
    @SerializedName("pinVenta")
    var salePin: String? = null

}