package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.GiroCaptureBoxDTO
import co.com.pagatodo.core.data.dto.GiroUserDTO
import com.google.gson.annotations.SerializedName

class RequestGiroModifyNotesDTO:RequestGiroBase() {

    @SerializedName("notas")
    var notes: String? = null

    @SerializedName("referencia")
    var reference: String? = null
}