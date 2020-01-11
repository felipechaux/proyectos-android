package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.CategoriesDTO
import com.google.gson.annotations.SerializedName

class ResponseQueryParameterDTO : BaseResponseDTO(){

    @SerializedName("categorias")
    var categories: List<CategoriesDTO>? = null

    @SerializedName("convenios")
    var agreements: List<String>? = null

}
