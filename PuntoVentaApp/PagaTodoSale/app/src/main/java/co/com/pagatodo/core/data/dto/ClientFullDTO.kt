package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class ClientFullDTO {

    @SerializedName("tipoIdentificacion")
    var typeId: String ? = null

    @SerializedName("identificacion")
    var id: String? = null

    @SerializedName("primerNombre")
    var firstName: String? = null

    @SerializedName("primerApellido")
    var lastName: String? = null

    @SerializedName("segundoApellido")
    var secondLastName: String? = null

    @SerializedName("direccion")
    var address: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("fechaExpedicionDocumento")
    var documentExpeditionDate: String? = null

    @SerializedName("bornDate")
    var bornDate: Boolean? = null

    @SerializedName("telefono")
    var telephone: String? = null

    @SerializedName("celular")
    var mobile: String? = null

    @SerializedName("enrolado")
    var isEnrolled: Boolean? = null

    @SerializedName("exoneradoHuella")
    var exoneratedFootprint: Boolean? = null

    @SerializedName("estrato")
    var stratum: String? = null

    @SerializedName("codTipoPersona")
    var personTypeCode: String? = null

    @SerializedName("remitente")
    var sender: Boolean? = null

    @SerializedName("huella")
    var fingerPrint: String? = null

    @SerializedName("huellaImg")
    var imgFingerPrint: String? = null

}