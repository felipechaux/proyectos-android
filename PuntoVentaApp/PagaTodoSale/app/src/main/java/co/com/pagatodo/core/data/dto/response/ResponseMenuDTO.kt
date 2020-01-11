package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MenuDTO
import com.google.gson.annotations.SerializedName

class ResponseMenuDTO:BaseResponseDTO() {


    @SerializedName("menus")
    var menus: List<MenuDTO>? = null
}