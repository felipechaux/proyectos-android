package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.BalotoBoardDTO
import com.google.gson.annotations.SerializedName

class RequestBalotoSendBetDTO {
    @SerializedName("canalId")
    var channelId: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("valor")
    var value: Int? = null

    @SerializedName("duration")
    var duration: Int? = null

    @SerializedName("gameName")
    var gameName: String? = null

    @SerializedName("price")
    var price: Int? = null

    @SerializedName("boards")
    var boardDTOS: List<BalotoBoardDTO>? = null

    @SerializedName("transactionTime")
    var transactionTime: Long? = null

    @SerializedName("serie1")
    var serie1: String? = null

    @SerializedName("serie2")
    var serie2: String? = null

    @SerializedName("secuenciaTransaccion")
    var sequenceTransaction: Int? = null

    @SerializedName("tipoTransaccionSolicitud")
    var transactionType: String? = null
}