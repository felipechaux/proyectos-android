package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class AgreementDTO {
    @SerializedName("id")
    var agreementId: String? = null

    @SerializedName("descripcion")
    var description: DescriptionAgreementDTO? = null
}