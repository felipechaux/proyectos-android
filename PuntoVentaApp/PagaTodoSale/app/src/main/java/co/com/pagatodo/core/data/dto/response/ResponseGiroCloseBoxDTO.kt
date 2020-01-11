package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseGiroCloseBoxDTO :BaseResponseDTO() {



    @SerializedName("lineasImpresion")
    var listPrint: List<String>? = null

}

