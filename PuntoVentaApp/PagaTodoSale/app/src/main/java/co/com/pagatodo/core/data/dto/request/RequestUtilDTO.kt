package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

open class RequestUtilDTO {
    @SerializedName("canalId")
    var canalId: String? = null
    @SerializedName("codigoOperacion")
    var operationCode: String? = null
    @SerializedName("codigoVendedor")
    var sellerCode: String? = null
    @SerializedName("codigoTerminal")
    var terminalCode: String? = null
    @SerializedName("tipoUsuario")
    var userType: String? = null
    @SerializedName("valores")
    var values: List<RequestUtilValueDTO>? = null
}

class RequestUtilValueDTO {
    @SerializedName("id")
    var id: String? = null
    @SerializedName("valor")
    var value: String? = null
}