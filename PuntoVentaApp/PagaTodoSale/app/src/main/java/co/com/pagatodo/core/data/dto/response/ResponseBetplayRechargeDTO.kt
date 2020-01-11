package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseBetplayRechargeDTO :BaseResponseDTO() {

    @SerializedName("usuario")
    var user: String? = null

    @SerializedName("serie1")
    var serie1: String? = null

    @SerializedName("serie2")
    var serie2: String? = null

    @SerializedName("milisegRta")
    var milisegRta: Int? = null

    @SerializedName("tramaRta")
    var tramaRta: String? = null

    @SerializedName("numDocumento")
    var documentNumber: String? = null

    @SerializedName("saldo")
    var balance: Int? = null

    @SerializedName("codigoSeguridad")
    var securityCode: String? = null

    @SerializedName("serie2Actual")
    var currentSerie2: String? = null

    @SerializedName("reimpresion")
    var isRePrint: Boolean? = null

    @SerializedName("synNromov")
    var synNromov: String? = null

    @SerializedName("estado")
    var status: String? = null

    @SerializedName("valor")
    var value: String? = null

}