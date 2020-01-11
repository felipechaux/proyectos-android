package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.GiroConceptDTO
import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseGiroCaptureDTO:BaseResponseDTO() {

    @SerializedName("token")
    var token: String? = null

    @SerializedName("codProveedor")
    var codeProvider: String? = null

    @SerializedName("codPostal")
    var codePostal: String? = null

    @SerializedName("consecutivo")
    var consecutive: String? = null

    @SerializedName("pin")
    var pin: String? = null

    @SerializedName("factura")
    var bill: String? = null

    @SerializedName("conceptos")
    var concepts: List<GiroConceptDTO>? = null


}