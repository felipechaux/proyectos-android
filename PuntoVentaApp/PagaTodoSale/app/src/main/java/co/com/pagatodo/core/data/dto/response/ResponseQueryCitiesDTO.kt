package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.CitiesDTO
import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseQueryCitiesDTO:BaseResponseDTO() {



    @SerializedName("ciudades")
    var cities: List<CitiesDTO>? = null





}