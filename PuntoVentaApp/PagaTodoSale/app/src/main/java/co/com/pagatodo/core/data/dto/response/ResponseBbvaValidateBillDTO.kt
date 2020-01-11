package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.BillRequestDTO
import com.google.gson.annotations.SerializedName

class ResponseBbvaValidateBillDTO : BaseResponseDTO() {

    @SerializedName("factura")
    var bill: BillRequestDTO? = null


}