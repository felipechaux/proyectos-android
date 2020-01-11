package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class TerminalDTO {
    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("descripcion")
    var description: String? = null

    @SerializedName("tipoTerminal")
    var terminalType: String? = null

    @SerializedName("idUnico")
    var uniqueId: Int? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("codMunicipio")
    var municipalityCode: String? = null

    @SerializedName("codOficina")
    var oficeCode: String? = null

    @SerializedName("tipoPuntoVenta")
    var typePointSale: String? = null

    @SerializedName("codPuntoVenta")
    var codePointSale: String? = null

    @SerializedName("direccion")
    var address: String? = null

    @SerializedName("codZona")
    var codeZone: String? = null

    @SerializedName("contrato")
    var contract: String? = null
}