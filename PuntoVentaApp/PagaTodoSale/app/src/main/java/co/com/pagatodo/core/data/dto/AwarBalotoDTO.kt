package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class AwarBalotoDTO {
    @SerializedName("div")
    var div: String = ""
    @SerializedName("ganadores")
    var winners: String = ""
    @SerializedName("cantidad")
    var quantity: String = ""
}