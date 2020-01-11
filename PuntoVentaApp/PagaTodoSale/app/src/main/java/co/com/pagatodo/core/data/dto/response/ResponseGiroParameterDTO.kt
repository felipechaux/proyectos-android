package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.DepartmentDTO
import co.com.pagatodo.core.data.dto.GiroTypeRequestsDTO
import co.com.pagatodo.core.data.dto.MessageDTO
import co.com.pagatodo.core.data.dto.ParameterDTO
import com.google.gson.annotations.SerializedName

class ResponseGiroParameterDTO : BaseResponseDTO() {


    @SerializedName("paises")
    var countrys: List<CountryDTO>? = null

    @SerializedName("parametros")
    var parameters: List<ParameterDTO>? = null

    @SerializedName("departamentos")
    var departaments: List<DepartmentDTO>? = null

    @SerializedName("tipoSolicitudes")
    var typeRequests: List<GiroTypeRequestsDTO>? = null

    @Transient
    var isload: Boolean = false


}