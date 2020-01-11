package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class QueryThirdDTO {

    @SerializedName("tipoIdentificacion")
    var typeId: String ? = null

    @SerializedName("identificacion")
    var id: String? = null


}