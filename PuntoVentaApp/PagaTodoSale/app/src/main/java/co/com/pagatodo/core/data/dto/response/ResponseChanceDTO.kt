package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class ResponseChanceDTO  :BaseResponseDTO(){
    @SerializedName("serie1")
    var serie1: String? = null
    @SerializedName("serie2")
    var serie2: String? = null
    @SerializedName("serialUnico")
    var uniqueSerial: String? = null
    @SerializedName("digitoChequeo")
    var digitChecking: String? = null
    @SerializedName("codigoSeguridad")
    var checkNumber: String? = null
    @SerializedName("valorTotalPagado")
    var totalValuePaid: Int? = null
    @SerializedName("valorTotalApostado")
    var totalValueBetting: Int? = null
    @SerializedName("valorTotalEncime")
    var totalValueOverloaded: Int? = null
    @SerializedName("valorTotalIva")
    var totalValueIva: Int? = null
    @SerializedName("fechaSorteo")
    var drawDate: String? = null
    @SerializedName("mensajePromocional")
    var promotionalMessage: String? = null
    @SerializedName("apuestasRespuesta")
    var answerBets: List<BetsAnswer>? = null

    @SerializedName("serie2Actual")
    var currentSerie2: String? = null
}


class BetsAnswer {
    @SerializedName("idApuesta")
    var idGame: String? = null
    @SerializedName("codigoModalidad")
    var modalityCode: String? = null
    @SerializedName("numeroApostado")
    var numberBet: String? = null
    @SerializedName("numeroOriginal")
    var originalNumber: String? = null
    @SerializedName("valorPagado")
    var paidValue: Int? = null
    @SerializedName("valorApostado")
    var valueBet: Int? = null
    @SerializedName("valorIva")
    var valueIva: Int? = null
}