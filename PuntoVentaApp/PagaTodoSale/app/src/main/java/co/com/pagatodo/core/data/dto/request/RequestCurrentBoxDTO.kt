package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestCurrentBoxDTO {
    @SerializedName("canalId")
    var canalId: String? = null
    @SerializedName("codigoVendedor")
    var sellerCode: String? = null
    @SerializedName("codigoTerminal")
    var terminalCode: String? = null
    @SerializedName("tipoUsuario")
    var userType: String? = null
    @SerializedName("tipoConsulta")
    var queryType: String? = null
    @SerializedName("fechaConsulta")
    var dateConsultation: String? = null
}