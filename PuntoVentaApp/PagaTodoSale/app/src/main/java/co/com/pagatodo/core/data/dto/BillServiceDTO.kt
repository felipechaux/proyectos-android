package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class BillServiceDTO {
    @SerializedName("idEmpresaServicio")
    var companyIdService: Int? = null

    @SerializedName("idServicio")
    var serviceId: String? = null

    @SerializedName("nombreServicio")
    var serviceName: String? = null

    @SerializedName("descripcionServicio")
    var serviceDescription: String? = null

    @SerializedName("imageServicio")
    var serviceImage: String? = null

    @SerializedName("expRegular")
    var regularExpression: String? = null

    @SerializedName("selected")
    var isSelected: Boolean? = null

    @SerializedName("pagosParciales")
    var isPartialPayment: Boolean? = null

    @SerializedName("porcentaje")
    var isPercentage: Boolean? = null

    @SerializedName("valorMinimo")
    var isMinValue: Boolean? = null

    @SerializedName("aceptaCodBarras")
    var isAcceptedBarCode: Boolean? = null

    @SerializedName("aceptaCargaPreviaFac")
    var isAcceptedPreviousLoadFac: Boolean? = null

    @SerializedName("parametrizaClaseEspecial")
    var isParameterizeSpecialClass: Boolean? = null

    @SerializedName("valorPagoParcial")
    var patialPayValue: Int? = null

    @SerializedName("cantDiasVencidos")
    var totalExpiredDays: Int? = null

    @SerializedName("idFormatoCodBarras")
    var formatIdBarcode: String? = null

    @SerializedName("nombreCodBarras")
    var nameBarCode: String? = null

    @SerializedName("claseEspecial")
    var specialClass: String? = null

    @SerializedName("claseCargarArchivo")
    var fileLoadClass: String? = null

    @SerializedName("claseGenerarArchivo")
    var generalFileClass: String? = null

    @SerializedName("estado")
    var status: String? = null

    @SerializedName("tipoCarga")
    var loadType: String? = null

    @SerializedName("fragmentoImpresion")
    var printFragment: String? = null

    @SerializedName("codigoConvenio")
    var agreementCode: String? = null

    @SerializedName("codBarras")
    var barCode: String? = null

    @SerializedName("ciudadSelected")
    var selectedCity: String? = null

    @SerializedName("ingreso")
    var isEntry: Boolean? = null
}