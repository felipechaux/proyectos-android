package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseGiroSendVoucherDTO:BaseResponseDTO() {



    @SerializedName("token")
    var token: String? = null

}