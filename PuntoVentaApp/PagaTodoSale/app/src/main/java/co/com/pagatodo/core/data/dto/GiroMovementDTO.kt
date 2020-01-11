package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class GiroMovementDTO {


    @SerializedName("moneda")
    var currency: String? = null

    @SerializedName("medioPago")
    var paymentMethod: String? = null

    @SerializedName("concepto")
    var concept: String? = null

    @SerializedName("fecha")
    var date: String? = null

    @SerializedName("hora")
    var hour: String? = null

    @SerializedName("referencia")
    var reference: String? = null

    @SerializedName("valor")
    var value: Int? = null

}