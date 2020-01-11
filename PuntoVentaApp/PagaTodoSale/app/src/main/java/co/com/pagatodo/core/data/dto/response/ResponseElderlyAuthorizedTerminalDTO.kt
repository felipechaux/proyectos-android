package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseElderlyAuthorizedTerminalDTO :BaseResponseDTO() {



    @SerializedName("token")
    var token: String? = null

    @SerializedName("autorizada")
    var authorized: Boolean? = null

}