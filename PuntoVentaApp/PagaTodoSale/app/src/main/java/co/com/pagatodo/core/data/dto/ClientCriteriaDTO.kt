package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class ClientCriteriaDTO {

    @SerializedName("tipoIdentificacion")
    var typeId: String ? = null

    @SerializedName("identificacion")
    var id: String? = null

    @SerializedName("primerNombre")
    var name: String? = null

    @SerializedName("primerApellido")
    var lastName: String? = null

    @SerializedName("enrolado")
    var isEnrolled: Boolean? = null

    @SerializedName("exoneradoHuella")
    var isExoneratedFootprint: Boolean? = null

    @SerializedName("remitente")
    var isSender: Boolean? = null



}