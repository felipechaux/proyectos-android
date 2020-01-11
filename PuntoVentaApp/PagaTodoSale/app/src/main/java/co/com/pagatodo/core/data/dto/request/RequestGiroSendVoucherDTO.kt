package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.ClientGiroDTO
import co.com.pagatodo.core.data.dto.GiroUserDTO
import com.google.gson.annotations.SerializedName

class RequestGiroSendVoucherDTO {

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

    @SerializedName("clienteOrigen")
    var clientGiro: ClientGiroDTO? = null

    @SerializedName("referencia")
    var reference: String? = null

    @SerializedName("tipoGiro")
    var typeGiro: String? = null

    @SerializedName("valor")
    var value: String? = null

    @SerializedName("serie1")
    var serie1: String? = null

    @SerializedName("serie2")
    var serie2: String? = null

    @SerializedName("fecha")
    var date: String? = null

    @SerializedName("hora")
    var hour: String? = null

    @SerializedName("tipoImpresora")
    var typePrint: String? = null

    @SerializedName("enviarEmail")
    var sendMail: Boolean? = null

    @SerializedName("impresion")
    var print: String? = null


}