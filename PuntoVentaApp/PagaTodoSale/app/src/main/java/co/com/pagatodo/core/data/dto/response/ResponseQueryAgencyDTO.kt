package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.AgencyDTO
import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseQueryAgencyDTO:BaseResponseDTO() {




    @SerializedName("agencias")
    var agencies: List<AgencyDTO>? = null

}