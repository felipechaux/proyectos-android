package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.PinDTO
import com.google.gson.annotations.SerializedName



class RequestQueryPinWalletDTO {
    @SerializedName("canalId")
    var channelId: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null

    @SerializedName("pin")
    var pin: PinDTO? = null
}