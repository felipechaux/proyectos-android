package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class ResponsePinQuickBetDTO:BaseResponseDTO() {

    @SerializedName("estado")
    var status: String? = null

    @SerializedName("milisegRta")
    var millisecondsResponse: String? = null

    @SerializedName("tramaRta")
    var plotResponse: String? = null

    @SerializedName("reimpresion")
    var reprint: Boolean? = null
}