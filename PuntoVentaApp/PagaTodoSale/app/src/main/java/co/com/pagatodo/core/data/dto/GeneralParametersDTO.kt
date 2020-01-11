package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class GeneralParametersDTO {
    @SerializedName("codigoRespuesta")
    var code: String? = null
    @SerializedName("exito")
    var isSuccess: Boolean = false
    @SerializedName("productos")
    var productos: List<GeneralProductDTO>? = null
    @SerializedName("loterias")
    var lotteries: List<LotteryDTO>? = null
    @SerializedName("parametros")
    var parameters: List<ParameterDTO>? = null
    @SerializedName("operadores")
    var operators: List<OperatorDTO>? = null
}