package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class CategoriesDTO {

    @SerializedName ("codCategoria")
    var codeCategorie : String? = null

    @SerializedName ("nombCategoria")
    var nameCategorie : String? = null

}