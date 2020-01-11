package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseSellSportingBetDTO:BaseResponseDTO() {

    @SerializedName("serie1")
    var serie1: String? = null

    @SerializedName("consecutivo")
    var consecutive: String? = null

    @SerializedName("fecha")
    var date: String? = null

    @SerializedName("hora")
    var hour: String? = null

    @SerializedName("codVerificacion")
    var verificationCode: String? = null

    @SerializedName("valorTotal")
    var totalValue: Int? = null

    @SerializedName("serie2Actual")
    var currentSerie2: String? = null

    @SerializedName("iva")
    var ValueIva: Int? = null

    @SerializedName("apostado")
    var ValueNeto: Int? = null
}