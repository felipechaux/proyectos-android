package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class ElderlyBeneficiaryPayDTO {

    @SerializedName("nombre")
    var name: String? = null

    @SerializedName("primerApellido")
    var lastName: String? = null

    @SerializedName("numDocumento")
    var id: String? = null

    @SerializedName("tipoDocumento")
    var typeId: String? = null

}