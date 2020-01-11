package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestWalletDTO {
    @SerializedName("canalId")
    var idChannel: String? = null
    @SerializedName("codigoVendedor")
    var sellerCode: String? = null
    @SerializedName("codigoTerminal")
    var terminalCode: String? = null
    @SerializedName("tipoUsuario")
    var userType: String? = null
    @SerializedName("fechaConsulta")
    var dateQuery: String? = null
}