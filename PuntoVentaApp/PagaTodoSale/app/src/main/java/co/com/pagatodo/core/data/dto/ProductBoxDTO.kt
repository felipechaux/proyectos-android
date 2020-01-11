package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class ProductBoxDTO {
    @SerializedName("codProducto")
    var productCode: String? = null
    @SerializedName("nombre")
    var name: String? = null
    @SerializedName("valorVenta")
    var saleValue: String? = null
}