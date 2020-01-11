package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseElderlyQueryIdDTO :BaseResponseDTO() {

    @SerializedName("token")
    var token: String? = null

    @SerializedName("cedulaTercero")
    var idThird: String? = null


}