package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestAuthTokenDTO {

    @SerializedName("direccionMac")
    var macAdress: String? = null

    @SerializedName("volEquipo")
    var volMachine: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("canalId")
    var canalId: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("identificacion")
    var id: String? = null

    @SerializedName("tokenFisico")
    var token: String? = null

}