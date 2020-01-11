package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseQueryCardBlackDTO:BaseResponseDTO() {

    @SerializedName("estaEnListaNegra")
    var isBlackList: Boolean? = null
}