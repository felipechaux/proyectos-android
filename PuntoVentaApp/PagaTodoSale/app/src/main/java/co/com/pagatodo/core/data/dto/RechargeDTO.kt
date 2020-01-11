package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class RechargeDTO {
    @SerializedName("numero")
    var number: String? = null
    @SerializedName("codOperador")
    var operatorCode: String? = null
    @SerializedName("montoRecarga")
    var rechargeAmount: String? = null
    @SerializedName("codPaquete")
    var pakageCode: String? = null


}