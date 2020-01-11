package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class BaseColsubsidioProductsDTO {

    @SerializedName("codigoProducto")
    var productCode: String? = null
    @SerializedName("nombreProducto")
    var productName: String? = null
    @SerializedName("descripcionProducto")
    var productDescription: String? = null

}