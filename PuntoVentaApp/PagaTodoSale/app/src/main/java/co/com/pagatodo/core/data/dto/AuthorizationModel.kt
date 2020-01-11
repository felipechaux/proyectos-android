package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class AuthorizationModel {

    @SerializedName("referencia")
    var reference: String? = null

    @SerializedName("notas")
    var notes: String? = null

    @SerializedName("tipoSolicitudes")
    var typeRequests: GiroTypeRequestsDTO? = null

    @SerializedName("cliente")
    var third: ThirdDTO? = null

}