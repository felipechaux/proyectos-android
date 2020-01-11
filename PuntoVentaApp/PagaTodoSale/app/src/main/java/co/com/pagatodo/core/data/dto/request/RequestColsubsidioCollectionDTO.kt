package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestColsubsidioCollectionDTO {

    @SerializedName("obligacion")
    var obligation: ObligationColsubsidioDTO? = null
    @SerializedName("valorRecaudado")
    var collectedValue: String? = null

}

class ObligationColsubsidioDTO {

    @SerializedName("codSistemaLegado")
    var systemLegacyCode: String? = null
    @SerializedName("valorFactura")
    var invoiceValue: String? = null
    @SerializedName("tipoPago")
    var paymentType: String? = null
    @SerializedName("valorRecaudado")
    var collectedValue: String? = null

}

class ClientCollectObligationDTO {
    @SerializedName("numeroDocumento")
    var documentNumber: String? = null
}