package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class AgreementsBbvaDTO {

    var agreementId: String? = null


    var description: String? = null


    var daneCode: String? = null


    var validateValue: String? = null

    @SerializedName("codConvenio")
    var agreementCod: String ? = null

    @SerializedName("nombreConvenio")
    var agreementName: String ? = null

}