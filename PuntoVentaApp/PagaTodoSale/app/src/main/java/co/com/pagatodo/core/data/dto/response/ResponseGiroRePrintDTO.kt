package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.*
import com.google.gson.annotations.SerializedName

class ResponseGiroRePrintDTO :BaseResponseDTO() {

    @SerializedName("codProveedor")
    var providerCode: String? = null

    @SerializedName("codPostal")
    var postalCode: String? = null

    @SerializedName("consecutivo")
    var consecutive: String? = null

    @SerializedName("pin")
    var pin: String? = null

    @SerializedName("factura")
    var bill: String? = null

    @SerializedName("conceptos")
    var concepts: List<GiroConceptDTO>? = null

    @SerializedName("agenciaOrigen")
    var agencyOrigen: AgencyDTO? = null

    @SerializedName("agenciaDestino")
    var agencyDestination: AgencyDTO? = null

    @SerializedName("clienteOrigen")
    var clientGiro: ClientGiroDTO? = null

    @SerializedName("clienteDestino")
    var clientDestination: ClientGiroDTO? = null

    @SerializedName("valorGiro")
    var giroValue: Int? = null




}