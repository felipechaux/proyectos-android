package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class UserDTO {

    @SerializedName("codigo")
    var code: String? = null
    @SerializedName("documentoIdentidad")
    var identificationDocument: String? = null
    @SerializedName("tipoDocumento")
    var documentType: String? = null
    @SerializedName("nombre")
    var name: String? = null
    @SerializedName("direccion")
    var address: String? = null
    @SerializedName("telefono")
    var phone: String? = null
    @SerializedName("codigoMunicipio")
    var municipalityCode: String? = null
    @SerializedName("codigoOficina")
    var officeCode: String? = null
    @SerializedName("zona")
    var zone: String? = null
    @SerializedName("tipoTecnologia")
    var techType: String? = null
    @SerializedName("codigoMunicipioVenta")
    var municipalityCodeSale: String? = null
}