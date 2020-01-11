package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestBalotoCheckTicketDTO {
    @SerializedName("canalId")
    var canalId: String? = null
    @SerializedName("codigoVendedor")
    var sellerCode: String? = null
    @SerializedName("codigoTerminal")
    var terminalCode: String? = null
    @SerializedName("codigoProducto")
    var productCode: String? = null
    @SerializedName("tipoUsuario")
    var userType: String? = null
    @SerializedName("serie1")
    var serie1: String? = null
    @SerializedName("serie2")
    var serie2: String? = null
    @SerializedName("ticketSerialNumber")
    var ticketSerialNumber: String? = null
}