package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class PackageDTO {
    @SerializedName("codigoPaquete")
    var packageCode: String? = null
    @SerializedName("nombrePaquete")
    var packageName: String? = null
    @SerializedName("valorPaquete")
    var packageValue: Float? = null
}