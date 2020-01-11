package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class ResponseCheckResultLotteryDTO :BaseResponseDTO() {

    @SerializedName("resultados")
    var results: List<ResultsDTO>? = null
}

class ResultsDTO{
    @SerializedName("nombrePremio")
    var namePrize: String? = null
    @SerializedName("valor")
    var value: String? = null
    @SerializedName("numero")
    var number: String? = null
    @SerializedName("serie")
    var serie: String? = null
    @SerializedName("cantidad")
    var quantity: String? = null
}