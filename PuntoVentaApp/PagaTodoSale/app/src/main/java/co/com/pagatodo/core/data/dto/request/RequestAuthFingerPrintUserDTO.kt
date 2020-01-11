package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.GiroUserDTO
import com.google.gson.annotations.SerializedName

class RequestAuthFingerPrintUserDTO {

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

    @SerializedName("usuario")
    var user: GiroUserDTO? = null

    @SerializedName("tipoIdentificacion")
    var idtype: String? = null

    @SerializedName("identificacion")
    var id: String? = null

    @SerializedName("huella")
    var fingerPrint: String? = null

}