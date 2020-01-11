package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseRaffleSendDTO:BaseResponseDTO() {

    @SerializedName("serialUnico")
    var uniqueSerial: String? = null

    @SerializedName("serie1")
    var serie1: String? = null

    @SerializedName("serie2")
    var serie2: String? = null

    @SerializedName("serie2Actual")
    var currentSerie2: String? = null

    @SerializedName("descripcionRifa")
    var raffleDescription: String? = null
}