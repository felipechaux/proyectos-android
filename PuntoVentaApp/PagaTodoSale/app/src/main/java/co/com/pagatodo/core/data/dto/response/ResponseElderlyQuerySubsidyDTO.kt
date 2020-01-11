package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.ElderlyPayDTO
import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseElderlyQuerySubsidyDTO  :BaseResponseDTO(){


    @SerializedName("token")
    var token: String? = null

    @SerializedName("pagos")
    var payments: List<ElderlyPayDTO>? = null

    @SerializedName("autorizada")
    var authorized: String? = null


}