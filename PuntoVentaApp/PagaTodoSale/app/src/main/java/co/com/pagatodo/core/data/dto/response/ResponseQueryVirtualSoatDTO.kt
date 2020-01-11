package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class ResponseQueryVirtualSoatDTO :BaseResponseDTO(){

    @SerializedName("entityCode")
    var entityCode: String? = null
    @SerializedName("placa")
    var licensePlate: String? = null
    @SerializedName("valor")
    var value: String? = null
    @SerializedName("fechaInicioVigencia")
    var beginDate: String? = null
    @SerializedName("marca")
    var brand: String? = null
    @SerializedName("color")
    var color: String? = null
    @SerializedName("nombrePropietario")
    var ownerName: String? = null
}