package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.ChanceDTO
import co.com.pagatodo.core.data.dto.LotteryDTO
import com.google.gson.annotations.SerializedName

class RequestChanceDTO {
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
    @SerializedName("valorSugerido")
    var suggestedValue: Double? = null
    @SerializedName("canalId")
    var canalId: String? = null
    @SerializedName("codigoTerminal")
    var terminalCode: String? = null
    @SerializedName("idPromocion")
    var promotionId: Int? = null
    @SerializedName("codigoVerificacion")
    var verificationCode: String? = null
    @SerializedName("transactionTime")
    var transactionTime: Long? = null
    @SerializedName("secuenciaTransaccion")
    var sequenceTransaction: Int? = null
    @SerializedName("chanceNumeros")
    var chanceNumbers: List<ChanceDTO>? = null
    @SerializedName("loterias")
    var lotteries: List<LotteryDTO>? = null
    @SerializedName("tipoTransaccionSolicitud")
    var transactionType: String? = null
}