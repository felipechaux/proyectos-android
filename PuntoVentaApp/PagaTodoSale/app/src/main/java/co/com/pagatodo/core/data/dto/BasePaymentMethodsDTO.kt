package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class BasePaymentMethodsDTO {

    @SerializedName("codigoMedioPago")
    var paymentMethodCode: String? = null
    @SerializedName("medioPago")
    var paymentMethod: String? = null
    @SerializedName("estado")
    var status: String? = null
    @SerializedName("referenciaMedioPago")
    var paymentMethodReference: String? = null
    @SerializedName("referencia2")
    var referenceTwo: String? = null
    @SerializedName("valorMedioPago")
    var paymentMethodValue: String? = null

}