package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import co.com.pagatodo.core.data.dto.PayBillDTO
import com.google.gson.annotations.SerializedName

class ResponsePayBillDTO:BaseResponseDTO() {

    @SerializedName("pagoFactura")
    var payBill: PayBillDTO? = null
}