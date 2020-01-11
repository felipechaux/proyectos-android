package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.GiroCaptureBoxDTO
import co.com.pagatodo.core.data.dto.GiroUserDTO
import com.google.gson.annotations.SerializedName

class RequestGiroMovementDTO:RequestGiroBase() {

    @SerializedName("fechaConsuta")
    var dateConsult: String? = null


}