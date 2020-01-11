package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseQuerySnrGetParametersDTO:BaseResponseDTO() {


    @SerializedName("estadoHTTP")
    var statusHTTP: Int? = null
    @SerializedName("exitoso")
    var successfull: Boolean? = null
    @SerializedName("json")
    var json: String? = null

    @SerializedName("idParametro")
    var idParameter: Int? = null
    @SerializedName("nombre")
    var name: String? = null
    @SerializedName("nombreSPT")
    var nameSPT: String? = null
    @SerializedName("cupoActual")
    var currentQuota: Int? = null
    @SerializedName("cupoAnterior")
    var previousQuota: Int? = null
    @SerializedName("administrador")
    var administrator: String? = null
    @SerializedName("estado")
    var status: String? = null

}