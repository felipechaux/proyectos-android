package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class BaseColsubsidioProductsFullDTO {

    @SerializedName("idProducto")
    var productId: Int? = null
    @SerializedName("codigoProducto")
    var productCode: String? = null
    @SerializedName("nombreProducto")
    var productName: String? = null
    @SerializedName("descripcionProducto")
    var productDescription: String? = null
    @SerializedName("tipoMovimiento")
    var movementType: String? = null
    @SerializedName("fkIdEstado")
    var fkIdStatus: String? = null

}