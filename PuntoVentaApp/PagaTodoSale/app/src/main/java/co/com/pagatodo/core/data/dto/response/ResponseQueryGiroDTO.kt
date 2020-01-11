package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.*
import com.google.gson.annotations.SerializedName

class ResponseQueryGiroDTO :BaseResponseDTO(){


    @SerializedName("agenciaOrigen")
    var agencyOrigen: AgencyDTO? = null

    @SerializedName("agenciaDestino")
    var agencyDestination: AgencyDTO? = null

    @SerializedName("clienteOrigen")
    var clientOrigin: ClientFullDTO? = null

    @SerializedName("clienteDestino")
    var clientDestination: ClientFullDTO? = null

    @SerializedName("conceptos")
    var concepts: List<GiroConceptDTO>? = null

    @SerializedName("valorGiro")
    var giroValue: Int? = null

    @SerializedName("numeroFactura")
    var bill: String? = null

    @SerializedName("prefijoFactura")
    var prefBill: String? = null

    @SerializedName("fechaGiro")
    var dateGiro: String? = null

    @SerializedName("horaGiro")
    var hourGiro: String? = null

    @SerializedName("notas")
    var notes: String? = null



}