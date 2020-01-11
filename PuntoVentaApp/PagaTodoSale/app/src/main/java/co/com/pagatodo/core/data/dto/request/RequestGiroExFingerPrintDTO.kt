package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.DocumentDTO
import co.com.pagatodo.core.data.dto.GiroCaptureBoxDTO
import co.com.pagatodo.core.data.dto.GiroUserDTO
import co.com.pagatodo.core.data.dto.ThirdDTO
import co.com.pagatodo.core.data.dto.response.GiroTypeRequestsMessagesDTO
import com.google.gson.annotations.SerializedName

class RequestGiroExFingerPrintDTO :RequestGiroBase(){

    @SerializedName("cliente")
    var third: ThirdDTO? = null

    @SerializedName("notas")
    var notes: String? = null

    @SerializedName("mensajeSolicitud")
    var requestsMessages: GiroTypeRequestsMessagesDTO? = null

    @SerializedName("documentos")
    var listDocument: List<DocumentDTO>? = null




}