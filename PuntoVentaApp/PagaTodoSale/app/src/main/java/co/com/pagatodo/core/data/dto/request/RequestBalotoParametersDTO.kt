package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestBalotoParametersDTO {
    @SerializedName("canalId")
    var channelId: String? = null
    @SerializedName("codigoVendedor")
    var sellerCode: String? = null
    @SerializedName("codigoTerminal")
    var terminalCode: String? = null
    @SerializedName("codigoProducto")
    var productCode: String? = null
    @SerializedName("tipoUsuario")
    var userType: String? = null
    @SerializedName("reporte")
    var report: String? = null
}