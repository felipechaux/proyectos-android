package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class ClientGiroDTO {

    @SerializedName("tipoIdentificacion")
    var typeId: String ? = null

    @SerializedName("identificacion")
    var id: String? = null

    @SerializedName("enrolado")
    var isEnrolled: Boolean? = null

    @SerializedName("exoneradoHuella")
    var isExoneratedFootprint: Boolean? = null

    @SerializedName("primerNombre")
    var firstName: String? = null

    @SerializedName("primerApellido")
    var lastName: String? = null

    @SerializedName("segundoApellido")
    var secondLastName: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("direccion")
    var address: String? = null

    @SerializedName("celular")
    var mobile: String? = null

    @SerializedName("telefono")
    var telephone: String? = null

    @SerializedName("huella")
    var fingerPrint: String? = null

    @SerializedName("huellaImg")
    var imgFingerPrint: String? = null



}