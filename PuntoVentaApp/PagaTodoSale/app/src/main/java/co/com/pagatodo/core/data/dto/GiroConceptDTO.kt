package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class GiroConceptDTO {

    @SerializedName("codigo")
    var code: String? = null

    @SerializedName("nombre")
    var name: String? = null

    @SerializedName("valor")
    var value: Int? = null

}