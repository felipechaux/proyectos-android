package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.*
import com.google.gson.annotations.SerializedName

class RequestGiroCaptureDTO {

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("canalId")
    var canalId: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("serie1")
    var serie1: String? = null

    @SerializedName("serie2")
    var serie2: String? = null

    @SerializedName("valor")
    var value: Int? = null

    @SerializedName("incluyeFlete")
    var includesFreight: Boolean? = null

    @SerializedName("usuario")
    var user: GiroUserDTO? = null

    @SerializedName("clienteOrigen")
    var clientGiro: ClientGiroDTO? = null

    @SerializedName("clienteDestino")
    var clientDestination: ClientGiroDTO? = null

    @SerializedName("caja")
    var box: GiroCaptureBoxDTO? = null

    @SerializedName("conceptos")
    var concepts: List<GiroConceptDTO>? = null

    @SerializedName("agenciaDestino")
    var agency: AgencyDTO? = null


    @SerializedName("observaciones")
    var observations: String? = null

    @SerializedName("codOperacionInusual")
    var unusualOperationsCode: String? = null

    @SerializedName("operacionInusual")
    var unusualOperations: String? = null




}