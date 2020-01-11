package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class ResponseThirdDTO{

    @SerializedName("tipoIdentificacion")
    var typeId: String? = null

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
    var dateExpeditionDocument: String? = null

    @SerializedName("fechaNacimiento")
    var birthDate: String? = null

    @SerializedName("telefono")
    var telephone: String? = null

    @SerializedName("celular")
    var mobile: String? = null

    @SerializedName("enrolado")
    var isEnrolled: Boolean? = null

    @SerializedName("exoneradoHuella")
    var isExoneratedFootprint: Boolean? = null

    @SerializedName("estrato")
    var stratum: String? = null

    @SerializedName("codTipoPersona")
    var codeTypePerson: String? = null

    @SerializedName("remitente")
    var isSender: Boolean? = null

    @SerializedName("codPaisExpedicion")
    var countryExpeditionCode: String? = null

    @SerializedName("codActividad")
    var codeActivity: String? = null

    @SerializedName("fechaEntradaPais")
    var dateEntryCountry: String? = null

    @SerializedName("fechaVencePasaporte")
    var dateExpirePassport: String? = null




}