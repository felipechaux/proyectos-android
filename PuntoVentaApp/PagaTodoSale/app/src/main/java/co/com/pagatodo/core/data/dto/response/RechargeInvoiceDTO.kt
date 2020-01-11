package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class RechargeInvoiceDTO {
    @SerializedName("serie1")
    var serie1: String? = null
    @SerializedName("serie2")
    var serie2: String? = null
    @SerializedName("serie2Actual")
    var currentSerie2: String? = null

    @SerializedName("numero")
    var number: String? = null
    @SerializedName("codOperador")
    var operatorCode: String? = null
    @SerializedName("codPaquete")
    var packageCode: String? = null
    @SerializedName("numeroAutorizacion")
    var authNumber: String? = null
    @SerializedName("idUnicoTerminal")
    var idTerminal: String? = null
    @SerializedName("horaRecarga")
    var hourRecharge: String? = null
    @SerializedName("fechaRecarga")
    var dateRecharge: String? = null
    @SerializedName("facturaPrefijo")
    var billPrefix: String? = null
    @SerializedName("facturaActual")
    var currentBill: String? = null
    @SerializedName("facturaMensaje")
    var billMessage: String? = null
}