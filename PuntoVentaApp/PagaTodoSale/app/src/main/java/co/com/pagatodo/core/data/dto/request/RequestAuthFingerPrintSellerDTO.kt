package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.FingerPrintDTO
import co.com.pagatodo.core.data.dto.GiroUserDTO
import com.google.gson.annotations.SerializedName

class RequestAuthFingerPrintSellerDTO {

    @SerializedName("direccionMac")
    var macAdress: String? = null

    @SerializedName("volEquipo")
    var volMachine: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("usuario")
    var user: String? = null

    @SerializedName("canalId")
    var canalId: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("tipoTercero")
    var thirdType: String? = null

    @SerializedName("huellaDTO")
    var fingerPrint: FingerPrintDTO? = null


}