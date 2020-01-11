package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.LotteryDTO
import co.com.pagatodo.core.data.dto.NumberDTO
import com.google.gson.annotations.SerializedName

class ResponseCheckNumberLotteryDTO :BaseResponseDTO() {

    @SerializedName("billetes")
    var tickets: List<ResponseLotteryNumberDTO>? = null
    @SerializedName("numeros")
    var numbers: List<ResponseLotteryNumberDTO>? = null
    @SerializedName("numero")
    var number: NumberDTO? = null
}

class ResponseLotteryNumberDTO {
    @SerializedName("numero")
    var number: String? = null
    @SerializedName("fraccion")
    var fraction: String? = null
    @SerializedName("fracciones")
    var fractions: String? = null
    @SerializedName("serie")
    var serie: String? = null
    @SerializedName("codigoBarras")
    var barcode: String? = null
    @SerializedName("loteria")
    var lottery: LotteryDTO? = null
}