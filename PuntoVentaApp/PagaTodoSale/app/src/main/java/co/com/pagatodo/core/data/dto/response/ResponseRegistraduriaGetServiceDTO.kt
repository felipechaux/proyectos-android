package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import co.com.pagatodo.core.data.dto.RegistraduriaServicesDTO
import com.google.gson.annotations.SerializedName

class ResponseRegistraduriaGetServiceDTO:BaseResponseDTO() {

    @SerializedName("lstServicios")
    var services: List<RegistraduriaServicesDTO>? = null







}