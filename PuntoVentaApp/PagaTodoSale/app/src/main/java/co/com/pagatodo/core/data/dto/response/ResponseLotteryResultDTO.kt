package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class ResponseLotteryResultDTO {

    class ResponseInternalLotteryResultDTO {
        @SerializedName("fecha")
        var date: String? = null
        @SerializedName("codLoteria")
        var lotteryCode: String? = null
        @SerializedName("nombreLoteria")
        var lotteryName: String? = null
        @SerializedName("numero")
        var number: String? = null
        @SerializedName("serie")
        var serie: String? = null
        @SerializedName("nombreCortoLoteria")
        var shortLotteryName: String? = null
    }

    @SerializedName("codigoRespuesta")
    var code: String? = null
    @SerializedName("exito")
    var isSuccess: Boolean = false
    @SerializedName("resultados")
    var results: List<ResponseInternalLotteryResultDTO>? = null
}