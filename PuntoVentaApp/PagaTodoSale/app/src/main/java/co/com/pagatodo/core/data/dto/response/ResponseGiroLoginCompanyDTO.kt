package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class ResponseGiroLoginCompanyDTO {
    @SerializedName("idEmpresa")
    var companyId: Int? = null

    @SerializedName("codEmpresa")
    var companyCode: String? = null

    @SerializedName("nombre")
    var companyName: String? = null

    @SerializedName("nit")
    var companyNit: String? = null

    @SerializedName("direccionEmpresa")
    var companyAddress: String? = null

    @SerializedName("telefonos")
    var companyPhone: String? = null

    @SerializedName("url")
    var companyUrl: String? = null
}