package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.BasePaymentMethodsDTO
import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseColsubsidioGetPaymentMethodsDTO :BaseResponseDTO() {

    @SerializedName("lstMedioPago")
    var paymentMethodList: List<BasePaymentMethodsDTO>? = null

}