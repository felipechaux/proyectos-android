package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class CountryDTO {

    @SerializedName("codigo")
    var code: String? = null

    @SerializedName("nombre")
    var name: String? = null


}