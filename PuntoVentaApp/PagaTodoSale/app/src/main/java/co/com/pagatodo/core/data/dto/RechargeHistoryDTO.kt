package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class RechargeHistoryDTO {
    @SerializedName("idRecarga")
    var responseCode: Long? = null
    @SerializedName("idTransaccionRecarga")
    var transactionId: Long? = null
    @SerializedName("idEmpresa")
    var companyId: Int? = null
    @SerializedName("numero")
    var number: String? = null
    @SerializedName("codOperador")
    var operatorCode: Int? = null
    @SerializedName("estado")
    var state: String? = null
    @SerializedName("montoRecarga")
    var rechargeAmount: Float? = null
    @SerializedName("numeroAutorizacion")
    var autorizationNumber: String? = null
    @SerializedName("horaRecarga")
    var hourRecharge: String? = null
    @SerializedName("fechaRecarga")
    var dateRecharge: String? = null
    @SerializedName("serie1")
    var serie1: String? = null
    @SerializedName("serie2")
    var serie2: String? = null
}