package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class ThirdDTO {

    @SerializedName("tipoIdentificacion")
    var idtype: String? = null

    @SerializedName("identificacion")
    var id: String? = null

    @SerializedName("codTipoPersona")
    var codeTypePerson: String? = null

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
    var dateExpeditionDocument: String? = null

    @SerializedName("fechaNacimiento")
    var birthDate: String? = null

    @SerializedName("telefono")
    var telephone: String? = null

    @SerializedName("celular")
    var mobile: String? = null

    @SerializedName("enrolado")
    var enrolled: Boolean? = null

    @SerializedName("exoneradoHuella")
    var exoneratedFootprint: Boolean? = null

    @SerializedName("estrato")
    var stratum: String? = null

    @SerializedName("codActividad")
    var codeActivity: String? = null

    @SerializedName("daneCiudadResidencia")
    var daneCityResidence: String? = null

    @SerializedName("codPaisExpedicion")
    var countryExpeditionCode: String? = null

    @SerializedName("daneCiudadExpedicion")
    var daneCityExpedition: String? = null

    @SerializedName("remitente")
    var sender: Boolean? = null

    @SerializedName("fechaEntradaPais")
    var dateEntryCountry: String? = null

    @SerializedName("fechaVencePasaporte")
    var dateExpirePassport: String? = null

    @Transient
    var biometrics: String? = null

    @Transient
    var fPrintThirdL: String? = null

    @Transient
    var fPrintThirdR: String? = null


}