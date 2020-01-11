package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.GiroCaptureBoxDTO
import co.com.pagatodo.core.data.dto.GiroTypeRequestsDTO
import co.com.pagatodo.core.data.dto.GiroUserDTO
import co.com.pagatodo.core.data.dto.ThirdDTO
import com.google.gson.annotations.SerializedName

class RequestGiroAuthorizationDTO :RequestGiroBase(){

    @SerializedName("referencia")
    var reference: String? = null

    @SerializedName("notas")
    var notes: String? = null

    @SerializedName("tipoSolicitud")
    var typeRequests: GiroTypeRequestsDTO? = null

    @SerializedName("cliente")
    var third: ThirdDTO? = null


}