package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestElderlyQuerySubsidyDTO {

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("canalId")
    var canalId: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null

    @SerializedName("login")
    var login: String? = null

    @SerializedName("numDocumento")
    var document: String? = null

    @SerializedName("tipoDocumento")
    var documentType: String? = null

}