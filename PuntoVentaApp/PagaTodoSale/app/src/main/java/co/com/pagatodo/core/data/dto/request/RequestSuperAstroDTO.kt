package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.LotteryDTO
import co.com.pagatodo.core.data.dto.SuperAstroDTO
import com.google.gson.annotations.SerializedName

class RequestSuperAstroDTO {

    @SerializedName("serie1")
    var serie1: String? = null
    @SerializedName("serie2")
    var serie2: String? = null
    @SerializedName("tipoUsuario")
    var userType: String? = null
    @SerializedName("codigoVendedor")
    var sellerCode: String? = null
    @SerializedName("codigoProducto")
    var productCode: String? = null
    @SerializedName("valor")
    var value: Double? = null
    @SerializedName("canalId")
    var canalId: String? = null
    @SerializedName("codigoTerminal")
    var terminalCode: String? = null
    @SerializedName("chanceNumeros")
    var chanceNumbers: List<SuperAstroDTO>? = null
    @SerializedName("loterias")
    var lotteries: List<LotteryDTO>? = null
    @SerializedName("transactionTime")
    var transactionTime: Long? = null
    @SerializedName("secuenciaTransaccion")
    var sequenceTransaction: Int? = null
    @SerializedName("tipoTransaccionSolicitud")
    var transactionType: String? = null
}