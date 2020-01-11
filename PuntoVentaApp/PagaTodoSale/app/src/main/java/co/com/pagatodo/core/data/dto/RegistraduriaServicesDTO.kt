package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class RegistraduriaServicesDTO {

    @SerializedName("id")
    var id: Int? = null

    @SerializedName("nombreServicio")
    var service: String? = null

    @SerializedName("valor")
    var value: Int? = null

}