package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class DescriptionAgreementDTO {
    @SerializedName("idEmpresaServicio")
    var idCompanyService: String? = null

    @SerializedName("nombreServicio")
    var nameService: String? = null

    @SerializedName("cantServicios")
    var sizeService: Int? = null

    @SerializedName("obligatorios")
    var isRequired: Boolean? = null

    @SerializedName("indexServicio")
    var indexService: Int? = null

    @SerializedName("codBarras")
    var barcode: String? = null

    @SerializedName("etiquetaEntrada")
    var startLabel: String? = null

    @SerializedName("empresa")
    var company: CompanyAgreementDTO? = null

    @SerializedName("idServicio")
    var serviceId: Int? = null

    @SerializedName("tipoCargaServicio")
    var loadTypeService: String? = null

    @SerializedName("direccionMac")
    var macAddress: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("volEquipo")
    var volEquip: String? = null
}