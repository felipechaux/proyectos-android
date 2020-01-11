package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.PinDTO
import com.google.gson.annotations.SerializedName

class RequestVirtualWalletActivatePinDTO {
    @SerializedName("serie1")
    var serie1: String? = null

    @SerializedName("serie2")
    var serie2: String? = null

    @SerializedName("valor")
    var value: String? = null

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

    @SerializedName("transactionTime")
    var transactionTime: Long? = null

    @SerializedName("secuenciaTransaccion")
    var sequenceTransaction: Int? = null

    @SerializedName("tipoTransaccionSolicitud")
    var transactionType: String? = null
}