package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.AgreementDTO
import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponsePayBillGetAgreementsDTO:BaseResponseDTO() {

    @SerializedName("listaItems")
    var agreements: List<AgreementDTO>? = null
}