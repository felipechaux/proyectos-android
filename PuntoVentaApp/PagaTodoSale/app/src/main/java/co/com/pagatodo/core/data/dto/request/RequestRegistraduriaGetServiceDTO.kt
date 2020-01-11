package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class RequestRegistraduriaGetServiceDTO {

    @SerializedName("direccionMac")
    var macAddress: String? = null

    @SerializedName("canalId")
    var canalId: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null

}