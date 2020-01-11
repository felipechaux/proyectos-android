package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.GiroUserDTO
import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseGiroUserDTO:BaseResponseDTO() {



    @SerializedName("usuario")
    var user: GiroUserDTO? = null

}