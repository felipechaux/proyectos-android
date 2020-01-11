package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class AuthDTO {
    @SerializedName("codigoRespuesta")
    var responseCode: String? = null
    @SerializedName("exito")
    var isSuccess: Boolean = false
    @SerializedName("fechaTransaccion")
    var transactionDate: String? = null
    @SerializedName("horaTransaccion")
    var transactionHour: String? = null
    @SerializedName("mensaje")
    var message: String? = null
    @SerializedName("token")
    var token: String? = null
    @SerializedName("idSesion")
    var sessionId: String? = null
    @SerializedName("usuario")
    var user: UserDTO? = null
    @SerializedName("papelerias")
    var stubs: List<StubDTO>? = null
    @SerializedName("terminal")
    var terminal: TerminalDTO? = null
    @SerializedName("parametros")
    var parameters: List<ParameterDTO>? = null
}