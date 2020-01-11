package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class GiroUserDTO{

    @SerializedName("usuario")
    var user: String ? = null

    @SerializedName("clave")
    var password: String ? = null

    @SerializedName("agencia")
    var agency: String ? = null

    @SerializedName("caducidadClave")
    var expirationKey: Int ? = null

    @SerializedName("agenciaNombre")
    var agencyName: String ? = null

    @SerializedName("agenciaDireccion")
    var agencyAddress: String ? = null


}