package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseCardRechargeDTO :BaseResponseDTO() {


    @SerializedName("serie1")
    var series1: String? = null

    @SerializedName("serie2")
    var series2: String? = null

    @SerializedName("serie2Actual")
    var series2Current: String? = null
}