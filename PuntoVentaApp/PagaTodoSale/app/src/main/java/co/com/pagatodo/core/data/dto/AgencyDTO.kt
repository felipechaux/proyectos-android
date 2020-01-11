package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class AgencyDTO {

    @SerializedName("codigo")
    var code: String ? = null

    @SerializedName("nombre")
    var name: String ? = null

    @SerializedName("direccion")
    var address: String ? = null

    @SerializedName("ciudad")
    var city: CitiesDTO ? = null

}