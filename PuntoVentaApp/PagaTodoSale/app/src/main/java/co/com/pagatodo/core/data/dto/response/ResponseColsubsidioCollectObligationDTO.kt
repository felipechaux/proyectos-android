package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.BaseColsubsidioProductsFullDTO
import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseColsubsidioCollectObligationDTO :BaseResponseDTO() {



    @SerializedName("horaTransaccion")
    var transactionTime: String? = null

    @SerializedName("codigoOperacion")
    var operationCode: String? = null

}