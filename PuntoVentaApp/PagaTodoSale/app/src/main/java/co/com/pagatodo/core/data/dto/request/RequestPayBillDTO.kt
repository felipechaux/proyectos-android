package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.BillDTO
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class RequestPayBillDTO {

    @SerializedName("login")
    var login: String? = null

    @SerializedName("canalId")
    var channelId: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null

    @SerializedName("volEquipo")
    var teamVol: String? = null

    @SerializedName("facturaDTO")
    var bill: BillDTO? = null
}