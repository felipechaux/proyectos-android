package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class BillRequestDTO {

    @SerializedName("numeroReferencia")
    var refNumber: String ? = null

    @SerializedName("codCategoria")
    var codeCategorie: String ? = null

    @SerializedName("nombreCategoria")
    var nameCategorie: String ? = null

    @SerializedName("convenio")
    var agreement: AgreementsBbvaDTO ? = null

    @SerializedName("valor")
    var value: Int = 0

    @SerializedName("codigoValidacion")
    var validationCode: String ? = null

    @SerializedName("descripcion")
    var description: String ? = null

    @SerializedName("fechaVencimiento")
    var expirationDate: String ? = null

    @SerializedName("fechaMaximaPago")
    var maximumPaymentDate: String ? = null

}