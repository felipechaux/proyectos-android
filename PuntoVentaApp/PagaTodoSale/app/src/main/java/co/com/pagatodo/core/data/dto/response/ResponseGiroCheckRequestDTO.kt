package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.GirorCheckRequestsDTO
import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseGiroCheckRequestDTO:BaseResponseDTO() {
    
    @SerializedName("solicitudes")
    var requests: List<GirorCheckRequestsDTO>? = null



}