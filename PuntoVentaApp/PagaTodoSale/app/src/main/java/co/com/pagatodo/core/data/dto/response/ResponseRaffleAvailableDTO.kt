package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseRaffleAvailableDTO :BaseResponseDTO(){


    @SerializedName("disponibilidad")
    var availability: String? = null
}