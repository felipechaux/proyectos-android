package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.LotteryDTO
import com.google.gson.annotations.SerializedName

class RequestCheckNumberLotteryDTO {
    @SerializedName("tipoUsuario")
    var userType: String? = null
    @SerializedName("codigoVendedor")
    var sellerCode: String? = null
    @SerializedName("codigoProducto")
    var productCode: String? = null
    @SerializedName("canalId")
    var canalId: String? = null
    @SerializedName("codigoTerminal")
    var terminalCode: String? = null
    @SerializedName("transactionTime")
    var transactionTime: Long? = null
    @SerializedName("secuenciaTransaccion")
    var transactionSequence: Int? = null
    @SerializedName("numero")
    var number: LotteryNumberDTO? = null
}

class LotteryNumberDTO {
    @SerializedName("numero")
    var number: String? = null
    @SerializedName("loteria")
    var lottery: LotteryDTO? = null
}