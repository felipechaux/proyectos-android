package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestPayuCollectingDTO {
    @SerializedName("count")
    var count: Boolean? = false
    @SerializedName("codigoTerminal")
    var terminalCode: String? = null
    @SerializedName("referenciaPago")
    var paymentReference: String? = null
    @SerializedName("valorFactura")
    var billValue: String? = null
    @SerializedName("direccionMac")
    var macAddress: String? = null
    @SerializedName("volEquipo")
    var deviceVolume: String? = null
    @SerializedName("codigoVendedor")
    var sellerCode: String? = null
    @SerializedName("codigoProducto")
    var productCode: String? = null
    @SerializedName("canalId")
    var channelId: String? = null
    @SerializedName("tipoUsuario")
    var userType: String? = null
}