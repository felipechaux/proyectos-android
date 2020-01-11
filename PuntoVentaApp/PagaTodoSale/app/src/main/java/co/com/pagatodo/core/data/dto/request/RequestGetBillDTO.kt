package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestGetBillDTO {
    @SerializedName("login")
    var login: String? = null

    @SerializedName("servicioSeleccionado")
    var servicioSeleccionado: ServiceSelectedDTO? = null

    @SerializedName("canalId")
    var channelId: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null
}

class ServiceSelectedDTO {
    @SerializedName("idEmpresaServicio")
    var companyIdService: String? = null

    @SerializedName("codBarras")
    var barCode: String? = null
}