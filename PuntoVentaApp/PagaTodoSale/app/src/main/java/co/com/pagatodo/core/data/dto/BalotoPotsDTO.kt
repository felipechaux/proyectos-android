package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class BalotoPotsDTO {
    @SerializedName("amount")
    var amount: String? = null

    @SerializedName("description")
    var description: String? = null
}