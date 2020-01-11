package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class ResponseGiroLoginUserDTO{

    @SerializedName("usuarioNombre")
    var userName: String ? = null

    @SerializedName("clave")
    var password: String ? = null

    @SerializedName("agencia")
    var agency: String ? = null

    @SerializedName("agenciaNombre")
    var agencyName: String ? = null

    @SerializedName("agenciaDireccion")
    var agencyAddress: String ? = null

    @SerializedName("agenciaTelefono")
    var agencyPhone: String ? = null

    @SerializedName("topeCaptacion")
    var topCaptation: Int ? = null

    @SerializedName("tipoUsuario")
    var userType: String ? = null

    @SerializedName("caducidadClave")
    var expirationKey: Int ? = null

    @SerializedName("codigoDane")
    var daneCode: String ? = null

    @SerializedName("documentoPrefijo")
    var prefixDocument: String ? = null

    @SerializedName("documentoDescripcion")
    var descriptionDocument: String ? = null

    @SerializedName("documentoInicial")
    var initialDocument: String ? = null

    @SerializedName("documentoFinal")
    var finalDocument: String ? = null

    @SerializedName("documentoResolucion")
    var resolutionDocument: String ? = null

}