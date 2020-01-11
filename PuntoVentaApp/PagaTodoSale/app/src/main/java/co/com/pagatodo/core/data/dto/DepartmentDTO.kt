package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class DepartmentDTO {

    @SerializedName("codigo")
    var code: String? = null

    @SerializedName("nombre")
    var name: String? = null

}