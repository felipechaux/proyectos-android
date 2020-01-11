package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class DocumentDTO {

    @SerializedName("nombre")
    var name: String? = null

    @SerializedName("documento")
    var document: String? = null

}