package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class ElderlyPayDTO  {

    @SerializedName("referenciaPago")
    var reference: String? = null

    @SerializedName("valorPago")
    var value: String? = null

    @SerializedName("beneficiarioPago")
    var beneficiaryPay: ElderlyBeneficiaryPayDTO? = null




}