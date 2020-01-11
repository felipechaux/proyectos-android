package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestQueryParameterDTO {

    @SerializedName("tipoUsuario")
    var userType: String ? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String ? = null

    @SerializedName("canalId")
    var canalId: String ? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String ? = null

    @SerializedName("codigoProducto")
    var productCode: String ? = null

}