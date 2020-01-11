package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class RequestColsubsidioGetInitialDataDTO {

    @SerializedName("canalId")
    var channelId: String? = null
    @SerializedName("codigoVendedor")
    var sellerCode: String? = null
    @SerializedName("codigoTerminal")
    var terminalCode: String? = null
    @SerializedName("tipoUsuario")
    var userType: String? = null
    @SerializedName("tipoDocumento")
    var documentType: String? = null
    @SerializedName("codigoProducto")
    var productCode: String? = null

}