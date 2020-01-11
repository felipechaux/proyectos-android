package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.PinDTO
import com.google.gson.annotations.SerializedName

class ResponseActivatePinDTO :BaseResponseDTO() {

    @SerializedName("pin")
    var pin: PinDTO? = null

    @SerializedName("serie1")
    var serie1: String? = null

    @SerializedName("serie2")
    var serie2: String? = null

    @SerializedName("serie2Actual")
    var currentSerie2: String? = null
}