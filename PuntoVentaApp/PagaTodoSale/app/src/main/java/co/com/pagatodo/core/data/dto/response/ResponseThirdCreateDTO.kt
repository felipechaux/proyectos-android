package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import co.com.pagatodo.core.data.dto.ThirdDTO
import com.google.gson.annotations.SerializedName

class ResponseThirdCreateDTO:BaseResponseDTO() {

    @SerializedName("tercero")
    var third: ThirdDTO? = null

}