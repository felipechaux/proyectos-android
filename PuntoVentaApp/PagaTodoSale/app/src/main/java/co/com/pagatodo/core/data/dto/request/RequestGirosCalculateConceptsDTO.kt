package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.GiroUserDTO
import co.com.pagatodo.core.data.dto.QueryThirdDTO
import com.google.gson.annotations.SerializedName

class RequestGirosCalculateConceptsDTO {

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("canalId")
    var canalId: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null

    @SerializedName("usuario")
    var user: GiroUserDTO? = null

    @SerializedName("clienteOrigen")
    var thirdOrigin: QueryThirdDTO? = null

    @SerializedName("clienteDestino")
    var thirdDestination: QueryThirdDTO? = null

    @SerializedName("valor")
    var value: Int? = null

    @SerializedName("incluyeFlete")
    var includesFreight: Boolean? = null

    @SerializedName("agenciaDestino")
    var agencyDestination: String? = null

}