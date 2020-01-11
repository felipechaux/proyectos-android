package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class FingerPrintDTO {

    @SerializedName("tipoIdentificacion")
    var idType: String? = null

    @SerializedName("numeroIdentificacion")
    var id: String? = null

    @SerializedName("templateIndiceDerecho")
    var fingerPrint: String? = null

    @SerializedName("enrolado")
    var enrolled: Boolean? = null

}