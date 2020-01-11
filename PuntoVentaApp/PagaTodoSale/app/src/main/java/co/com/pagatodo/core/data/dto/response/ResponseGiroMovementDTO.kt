package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.GiroMovementDTO
import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseGiroMovementDTO:BaseResponseDTO() {



    @SerializedName("movimientos")
    var movements: List<GiroMovementDTO>? = null

    @SerializedName("saldoInicial")
    var valueInit: Int? = null

    @SerializedName("saldoTurno")
    var valueFinish: Int? = null

    @SerializedName("totalIngresos")
    var incomer: Int? = null

    @SerializedName("totalEgresos")
    var egress: Int? = null



}