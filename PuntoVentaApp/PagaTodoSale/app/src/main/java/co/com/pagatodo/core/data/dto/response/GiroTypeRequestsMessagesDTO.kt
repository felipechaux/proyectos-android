package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class GiroTypeRequestsMessagesDTO {

    @SerializedName("codigo")
    var code: String? = null

    @SerializedName("descripcion")
    var description: String? = null
}