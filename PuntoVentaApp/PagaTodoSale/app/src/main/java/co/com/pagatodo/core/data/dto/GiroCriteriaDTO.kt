package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class GiroCriteriaDTO {

    @SerializedName("agenciaOrigen")
    var agencyOrigen: AgencyDTO? = null

    @SerializedName("clienteOrigen")
    var clientOrigin: ClientCriteriaDTO? = null

    @SerializedName("clienteDestino")
    var clientDestination: ClientCriteriaDTO? = null

    @SerializedName("conceptos")
    var concepts: List<GiroConceptDTO>? = null

    @SerializedName("valorGiro")
    var giroValue: Int? = null

    @SerializedName("numeroReferencia")
    var reference: String? = null

    @SerializedName("estado")
    var status: String? = null



}