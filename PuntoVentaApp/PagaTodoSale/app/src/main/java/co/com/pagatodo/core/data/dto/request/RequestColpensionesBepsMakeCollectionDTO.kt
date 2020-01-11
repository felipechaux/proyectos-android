package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestColpensionesBepsMakeCollectionDTO {
    @SerializedName("serie1")
    var serie1: String? = null

    @SerializedName("serie2")
    var serie2: String? = null

    @SerializedName("canalId")
    var channelId: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("valor")
    var value: String? = null

    @SerializedName("tipoDocumento")
    var documentType: String? = null

    @SerializedName("numeroDocumento")
    var documentNumber: String? = null

    @SerializedName("diaNacimiento")
    var birthday: String? = null

    @SerializedName("mesNacimiento")
    var birthmonth: String? = null

    @SerializedName("telefono")
    var phone: String? = null
}