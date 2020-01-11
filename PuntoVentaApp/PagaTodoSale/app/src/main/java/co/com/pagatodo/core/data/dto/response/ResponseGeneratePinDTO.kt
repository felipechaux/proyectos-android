package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class ResponseGeneratePinDTO :BaseResponseDTO() {


    @SerializedName("milisegRta")
    var millisecondsResponse: String? = null

    @SerializedName("tramaRta")
    var plotResponse: String? = null

    @SerializedName("numDocumento")
    var numberDocument: String? = null

    @SerializedName("reimpresion")
    var reprint: Boolean? = null

    @SerializedName("codigoSeguridad")
    var securityCode: String? = null

    @SerializedName("serie1")
    var serie1: String? = null

    @SerializedName("serie2")
    var serie2: String? = null

    @SerializedName("serie2Actual")
    var currentSerie2: String? = null

}