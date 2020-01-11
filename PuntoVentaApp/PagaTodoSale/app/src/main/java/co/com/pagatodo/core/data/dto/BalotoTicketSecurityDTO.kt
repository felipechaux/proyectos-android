package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class BalotoTicketSecurityDTO {
    @SerializedName("docToken")
    var docToken: String? = null

    @SerializedName("secureDocToken")
    var secureDocToken: String? = null

    @SerializedName("keyModifier")
    var keyModifier: String? = null
}