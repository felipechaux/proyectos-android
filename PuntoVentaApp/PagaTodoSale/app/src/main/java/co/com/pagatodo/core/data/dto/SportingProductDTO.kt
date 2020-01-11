package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class SportingProductDTO {
    @SerializedName("idProducto")
    var productId: Int? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null

    @SerializedName("nombreProducto")
    var productName: String? = null

    @SerializedName("iva")
    var iva: Int? = null

    @SerializedName("lstEventos")
    var events: List<SportingEventDTO>? = null
}