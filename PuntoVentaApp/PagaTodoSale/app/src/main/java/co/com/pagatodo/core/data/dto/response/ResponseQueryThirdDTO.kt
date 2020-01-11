package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import co.com.pagatodo.core.data.dto.ThirdCityDTO
import com.google.gson.annotations.SerializedName

class ResponseQueryThirdDTO:BaseResponseDTO() {


    @SerializedName("tercero")
    var third: ResponseThirdDTO? = null

    @SerializedName("ciudadExpedicion")
    var cityExpedition: ThirdCityDTO? = null

    @SerializedName("ciudadResidencia")
    var cityResidence: ThirdCityDTO? = null



}