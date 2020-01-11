package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestRaffleSendDTO {

    @SerializedName("canalId")
    var channelId: String? = null

    @SerializedName("codigoRifa")
    var raffleCode: String? = null

    @SerializedName("numero")
    var numberRaffle: String? = null

    @SerializedName("serie1")
    var serie1: String? = null

    @SerializedName("serie2")
    var serie2: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("transactionTime")
    var transactionTime: Long? = null

    @SerializedName("secuenciaTransaccion")
    var sequenceTransaction: Int? = null

    @SerializedName("tipoTransaccionSolicitud")
    var transactionType: String? = null
}