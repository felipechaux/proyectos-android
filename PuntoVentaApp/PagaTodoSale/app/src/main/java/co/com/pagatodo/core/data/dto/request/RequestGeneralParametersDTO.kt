package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestGeneralParametersDTO {
    @SerializedName("canalId")
    var canalId: String? = null
    @SerializedName("codigoTerminal")
    var terminalCode: String? = null
    @SerializedName("codigoVendedor")
    var sellerCode: String? = null
    @SerializedName("tipoUsuario")
    var userType: String? = null
}