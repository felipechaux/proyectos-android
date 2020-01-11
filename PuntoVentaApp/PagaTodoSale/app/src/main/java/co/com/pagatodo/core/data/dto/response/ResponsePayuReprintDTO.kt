package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import co.com.pagatodo.core.data.dto.TransactionsDTO
import com.google.gson.annotations.SerializedName

class ResponsePayuReprintDTO:BaseResponseDTO() {

    @SerializedName("count")
    var count: Boolean? = false
    @SerializedName("cantidadMovimientos")
    var transactionQuantity: String? = null
    @SerializedName("piePagina")
    var pageFoot: String? = null
    @SerializedName("movimientos")
    var transactions: List<TransactionsDTO>? = null
}