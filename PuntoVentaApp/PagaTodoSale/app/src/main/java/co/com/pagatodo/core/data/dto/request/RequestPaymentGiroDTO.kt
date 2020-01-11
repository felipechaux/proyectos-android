package co.com.pagatodo.core.data.dto.request


import co.com.pagatodo.core.data.dto.*
import com.google.gson.annotations.SerializedName

class RequestPaymentGiroDTO {


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

    @SerializedName("usuario")
    var user: GiroUserDTO? = null

    @SerializedName("clienteOrigen")
    var clientGiro: ClientGiroDTO? = null

    @SerializedName("clienteDestino")
    var clientDestination: ClientGiroDTO? = null

    @SerializedName("agenciaOrigen")
    var agencyOrigin: AgencyDTO? = null

    @SerializedName("agenciaDestino")
    var agencyDestination: AgencyDTO? = null

    @SerializedName("caja")
    var box: GiroCaptureBoxDTO? = null

    @SerializedName("numeroFactura")
    var bill: String? = null

    @SerializedName("prefijoFactura")
    var prefBill: String? = null

    @SerializedName("codOperacionInusual")
    var unusualOperationsCode: String? = null

    @SerializedName("operacionInusual")
    var unusualOperations: String? = null

    @SerializedName("notas")
    var notes: String? = null

    @SerializedName("referencia")
    var reference: String? = null

    @SerializedName("conceptos")
    var concepts: List<GiroConceptDTO>? = null

}

class AgencyCodDTO{
    @SerializedName("codigo")
    var code: String? = null
}
