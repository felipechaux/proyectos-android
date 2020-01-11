package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.DepartmentDTO
import co.com.pagatodo.core.data.dto.GiroTypeRequestsDTO
import co.com.pagatodo.core.data.dto.MessageDTO
import co.com.pagatodo.core.data.dto.ParameterDTO
import com.google.gson.annotations.SerializedName

class ResponseElderlyParameterDTO {

    @SerializedName("codigoRespuesta")
    var responseCode: String? = null

    @SerializedName("exito")
    var isSuccess: Boolean? = null

    @SerializedName("fechaTransaccion")
    var transactionDate: String? = null

    @SerializedName("horaTransaccion")
    var transactionHour: String? = null

    @SerializedName("mensajes")
    var messages: List<MessageDTO>? = null

    @SerializedName("paises")
    var countrys: List<CountryDTO>? = null

    @SerializedName("parametros")
    var parameters: List<ParameterDTO>? = null

    @SerializedName("departamentos")
    var departaments: List<DepartmentDTO>? = null

    @SerializedName("tipoSolicitudes")
    var typeRequests: List<GiroTypeRequestsDTO>? = null

}