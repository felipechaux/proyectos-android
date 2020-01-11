package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.PinDTO
import com.google.gson.annotations.SerializedName

class ResponseQueryPinDTO:BaseResponseDTO() {

    @SerializedName("pin")
    var pin: PinDTO? = null
}