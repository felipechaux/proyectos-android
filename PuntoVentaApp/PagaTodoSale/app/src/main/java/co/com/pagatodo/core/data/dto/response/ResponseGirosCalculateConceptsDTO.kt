package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.GiroConceptDTO
import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseGirosCalculateConceptsDTO:BaseResponseDTO() {



    @SerializedName("conceptos")
    var concepts: List<GiroConceptDTO>? = null


}