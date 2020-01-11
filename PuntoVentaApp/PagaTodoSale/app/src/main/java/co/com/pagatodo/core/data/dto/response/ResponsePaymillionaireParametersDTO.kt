package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class PayMillionaireModeDTO {
    @SerializedName("codigo")
    var code: String? = null
    @SerializedName("nombre")
    var name: String? = null
    @SerializedName("valor")
    var value: String? = null
    @SerializedName("codigoProducto")
    var productCode: String? = null
    @SerializedName("acumulado")
    var accumulated: String? = null
    @SerializedName("cantCifras")
    var numberOfDigits: String? = null
}

class ResponsePaymillionaireParametersDTO:BaseResponseDTO() {

    @SerializedName("lstModalidades")
    var modes: List<PayMillionaireModeDTO>? = null
}