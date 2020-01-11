package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class CompanyAgreementDTO {
    @SerializedName("idEmpresa")
    var companyId: Int? = null

    @SerializedName("nombreEmpresa")
    var companyName: String? = null

    @SerializedName("descripcionEmpresa")
    var companyDescription: String? = null

    @SerializedName("nitEmpresa")
    var companyDocument: String? = null

    @SerializedName("direccionEmpresa")
    var companyAddress: String? = null

    @SerializedName("telefonoEmpresa")
    var companyPhone: String? = null

    @SerializedName("faxEmpresa")
    var companyFax: String? = null

    @SerializedName("afectaCaja")
    var affectBox: String? = null
}