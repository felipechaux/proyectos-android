package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class MenuDTO {
    @SerializedName("nombre")
    var name: String? = null

    @SerializedName("label")
    var label: String? = null

    @SerializedName("descripcion")
    var description: String? = null

    @SerializedName("estado")
    var status: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null
}