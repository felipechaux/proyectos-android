package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.LotteryDTO
import com.google.gson.annotations.SerializedName

class RequestLotteryNumberDTO {
    @SerializedName("numero")
    var number: String? = null
    @SerializedName("fracciones")
    var fractions: String? = null
    @SerializedName("serie")
    var serie: String? = null
    @SerializedName("loteria")
    var lottery: LotteryDTO? = null
}