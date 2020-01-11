package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.NumberDTO
import com.google.gson.annotations.SerializedName

class ResponsePayPhysicalLotteryDTO:BaseResponseDTO() {



    @SerializedName("digitoChequeo")
    var checkDigit: String? = null
    @SerializedName("serie2Actual")
    var currentSerie2: String? = null
    @SerializedName("numeros")
    var numbers: List<NumberDTO>? = null
}