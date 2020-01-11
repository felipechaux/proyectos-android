package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.CollectionSnrFullDTO
import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseQuerySnrMakeCollectionsDTO:BaseResponseDTO() {


    @SerializedName("estadoHTTP")
    var statusHTTP: Int? = null
    @SerializedName("exitoso")
    var successfull: Boolean? = null
    @SerializedName("json")
    var json: String? = null
    @SerializedName("recaudo")
    var collection: CollectionSnrFullDTO? = null


}