package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BalotoTaxDTO {
    @SerializedName("taxId")
    var taxId: String? = null

    @SerializedName("taxAmount")
    var taxAmount: Int? = null

    @SerializedName("taxRate")
    var taxRate: Int? = null

    @SerializedName("grossAmount")
    var grossAmount: Int? = null

    @SerializedName("netAmount")
    var netAmount: Int? = null
}