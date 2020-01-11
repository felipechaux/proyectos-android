package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.BaseColsubsidioProductsDTO
import com.google.gson.annotations.SerializedName

class ResponseColsubsidioGetProductsDTO {

    @SerializedName("exito")
    var isSuccess: Boolean? = false
    @SerializedName("lstProducto")
    var productList: List<BaseColsubsidioProductsDTO>? = null
}