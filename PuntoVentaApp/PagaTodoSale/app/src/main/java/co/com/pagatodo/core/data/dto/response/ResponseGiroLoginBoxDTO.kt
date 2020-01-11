package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class ResponseGiroLoginBoxDTO {

    @SerializedName("idCaja")
    var boxId: String? = null

    @SerializedName("codCaja")
    var boxCode: String? = null

    @SerializedName("descripcion")
    var boxDescription: String? = null

    @SerializedName("turnoHoraFinal")
    var boxFinalHourShift: String? = null

}