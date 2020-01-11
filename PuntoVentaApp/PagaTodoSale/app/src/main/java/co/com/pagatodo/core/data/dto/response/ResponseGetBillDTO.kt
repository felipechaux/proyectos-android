package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.BillDTO
import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseGetBillDTO  :BaseResponseDTO(){



    @SerializedName("factura")
    var bill: BillDTO? = null
}