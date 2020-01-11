package co.com.pagatodo.core.data.dto

import co.com.pagatodo.core.data.dto.response.GiroTypeRequestsMessagesDTO
import com.google.gson.annotations.SerializedName

class GiroTypeRequestsDTO {

    @SerializedName("codigo")
    var code: String? = null

    @SerializedName("descripcion")
    var description: String? = null

    @SerializedName("impresion")
    var printer: String? = null

    @SerializedName("mensajes")
    var messages: List<GiroTypeRequestsMessagesDTO>? = null

}