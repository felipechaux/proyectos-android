package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.RechargeHistoryDTO
import com.google.gson.annotations.SerializedName

class ResponseRechargeHistoryDTO :BaseResponseDTO(){


    
    @SerializedName("recargas")
    var recharges: List<RechargeHistoryDTO>? = null
}