package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.ProductBoxDTO
import com.google.gson.annotations.SerializedName

class ResponseCurrentBoxDTO :BaseResponseDTO() {




    @SerializedName("venta")
    var sale: Int? = null
    @SerializedName("loteria")
    var lottery: Int? = null
    @SerializedName("servicios")
    var services: Int? = null
    @SerializedName("otros")
    var others: String? = null
    @SerializedName("ventaTotal")
    var totalSale: String? = null
    @SerializedName("cuadreProductos")
    var productsBox: List<ProductBoxDTO>? = null
}