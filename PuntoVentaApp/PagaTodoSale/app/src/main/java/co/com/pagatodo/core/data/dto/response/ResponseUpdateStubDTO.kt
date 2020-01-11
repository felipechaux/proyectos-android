package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class ResponseUpdateStubDTO {
    @SerializedName("codigoRespuesta")
    var responseCode: String? = null

    @SerializedName("exito")
    var isSuccess: Boolean? = null

    @SerializedName("fechaTransaccion")
    var transactionDate: String? = null

    @SerializedName("horaTransaccion")
    var transactionHour: String? = null

    @SerializedName( "mensaje")
    var message: String? = null

    @SerializedName("valores")
    var values: List<ValuesUpdateStubDTO>? = null
}

class ValuesUpdateStubDTO {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("valor")
    var value: String? = null
}