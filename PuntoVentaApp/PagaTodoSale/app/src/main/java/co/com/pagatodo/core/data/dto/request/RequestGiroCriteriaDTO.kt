package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.ClientSmallDTO
import co.com.pagatodo.core.data.dto.GiroCaptureBoxDTO
import co.com.pagatodo.core.data.dto.GiroUserDTO
import com.google.gson.annotations.SerializedName

class RequestGiroCriteriaDTO:RequestGiroBase() {

    @SerializedName("fechaInicio")
    var dateStart: String? = null

    @SerializedName("fechaFin")
    var dateEnd: String? = null

    @SerializedName("cliente")
    var client: ClientSmallDTO? = null

    @SerializedName("tipoTercero")
    var typeThird: String? = null

    @SerializedName("referencia")
    var reference: String? = null

}