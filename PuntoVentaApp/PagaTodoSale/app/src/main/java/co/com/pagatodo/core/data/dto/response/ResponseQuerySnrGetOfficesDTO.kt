package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseQuerySnrGetOfficesDTO :BaseResponseDTO(){


    @SerializedName("mensajeError")
    var errorMessage: String? = null

}