package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ResponseQueryWinnersDTO :BaseResponseDTO(){
    @SerializedName("serie1")
    var serie1: String? = null

    @SerializedName("serie2")
    var serie2: String? = null

    @SerializedName("serialUnico")
    var uniqueSerial: String? = null

    @SerializedName("valorPagar")
    var payValue: Int? = null

    @SerializedName("valorPremio")
    var prizeValue: Int? = null

    @SerializedName("valorRetencion")
    var retentionValue: Int? = null

    @SerializedName("valorMegapleno")
    var valueMegapleno: Int? = null

    @SerializedName("valorSuperpleno")
    var valueSuperpleno: Int? = null

    @SerializedName("valorDirecto")
    var valueDirecto: Int? = null

    @SerializedName("valorCombinado")
    var valueCombinado: Int? = null

    @SerializedName("valorPata")
    var valuePata: Int? = null

    @SerializedName("valorUna")
    var valueUna: Int? = null
}