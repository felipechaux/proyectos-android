package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.GiroUserDTO

import com.google.gson.annotations.SerializedName

class RequestGiroLoginDTO {

    @SerializedName("tipoUsuario")
    var userType: String ? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String ? = null

    @SerializedName("canalId")
    var canalId: String ? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String ? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null

    @SerializedName("usuario")
    var user: GiroUserDTO? = null

}