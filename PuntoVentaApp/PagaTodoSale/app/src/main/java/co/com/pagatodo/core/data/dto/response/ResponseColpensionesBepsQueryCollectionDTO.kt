package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class ResponseColpensionesBepsQueryCollectionDTO :BaseResponseDTO() {


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

    @SerializedName("txtTransaccionImpresion")
    var printTransactionMessage: String? = null

    @SerializedName("codArriendo")
    var rentCode: String? = null

    @SerializedName("numCopiasImpresion")
    var printNumberCopies: String? = null
}