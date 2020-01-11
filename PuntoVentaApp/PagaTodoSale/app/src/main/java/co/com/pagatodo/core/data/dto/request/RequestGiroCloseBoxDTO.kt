package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.GiroCaptureBoxDTO
import co.com.pagatodo.core.data.dto.GiroUserDTO
import com.google.gson.annotations.SerializedName

class RequestGiroCloseBoxDTO:RequestGiroBase() {

    @SerializedName("saldoInicial")
    var bannerOpenning: Int? = null

    @SerializedName("saldoTurno")
    var balance: Int? = null

    @SerializedName("totalIngresos")
    var bannerIncome: Int? = null

    @SerializedName("totalEgresos")
    var bannerExpenses: Int? = null


}

