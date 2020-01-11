package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseElderlyAuthenticateFootprintDTO  :BaseResponseDTO(){


    @SerializedName("token")
    var token: String? = null

    @SerializedName("autenticado")
    var authenticated: Boolean? = null

    @SerializedName("nombreHuella")
    var footprintName: String? = null


}