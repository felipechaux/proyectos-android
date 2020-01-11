package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class ResponseColpensionesBepsMakeCollectionDTO :BaseResponseDTO() {





    @SerializedName("numeroAutorizacion")
    var authNumber: String? = null

    @SerializedName("numeroComprobante")
    var voucherNumber: String? = null

    @SerializedName("nombreBeneficiario")
    var beneficiaryName: String? = null

    @SerializedName("tipoDocumento")
    var documentType: String? = null

    @SerializedName("numeroDocumento")
    var documentNumber: String? = null

    @SerializedName("valor")
    var value: String? = null

    @SerializedName("identificador")
    var identifier: String? = null

    @SerializedName("motivoRechazo")
    var rejectionReason: String? = null

    @SerializedName("txtTransaccionImpresion")
    var printMessageTransaction: String? = null

    @SerializedName("codArriendo")
    var rentCode: String? = null

    @SerializedName("numCopiasImpresion")
    var printNumberCopies: String? = null

    @SerializedName("serie1")
    var serie1: String? = null

    @SerializedName("serie2")
    var serie2: String? = null

    @SerializedName("serie2Actual")
    var currentSerie2: String? = null
}