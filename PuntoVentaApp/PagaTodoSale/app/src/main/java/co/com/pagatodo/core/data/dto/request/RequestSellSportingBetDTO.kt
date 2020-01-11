package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.SportingEventDTO
import co.com.pagatodo.core.data.dto.SportingProductDTO
import com.google.gson.annotations.SerializedName

class RequestSellSportingBetDTO {
    @SerializedName("canalId")
    var channelId: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("serie")
    var serie: String? = null

    @SerializedName("consecutivo")
    var consecutive: String? = null

    @SerializedName("valor")
    var value: Int? = null

    @SerializedName("evento")
    var event: SportingEventDTO? = null

    @SerializedName("producto")
    var product: SportingProductDTO? = null

    @SerializedName("tipoTransaccionSolicitud")
    var transactionType: String? = null

    @SerializedName("transactionTime")
    var transactionTime: Long? = null

    @SerializedName("secuenciaTransaccion")
    var sequenceTransaction: Int? = null
}