package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseBbvaDTO:BaseResponseDTO() {

    @SerializedName("serialUnico")
    var uniqueSerial: String? = null

    @SerializedName("numComprobante")
    var numComprobante: String? = null

}