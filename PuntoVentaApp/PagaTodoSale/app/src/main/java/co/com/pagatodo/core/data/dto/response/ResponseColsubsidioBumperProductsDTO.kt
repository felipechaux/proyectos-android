package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.BaseColsubsidioProductsFullDTO
import com.google.gson.annotations.SerializedName

class ResponseColsubsidioBumperProductsDTO {

    @SerializedName("lstProducto")
    var productList: List<BaseColsubsidioProductsFullDTO>? = null

}