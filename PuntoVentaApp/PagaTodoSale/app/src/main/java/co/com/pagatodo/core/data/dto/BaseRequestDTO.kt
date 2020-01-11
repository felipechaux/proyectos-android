package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

open class BaseRequestDTO{

    @SerializedName("canalId")
    var canalId: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null
}