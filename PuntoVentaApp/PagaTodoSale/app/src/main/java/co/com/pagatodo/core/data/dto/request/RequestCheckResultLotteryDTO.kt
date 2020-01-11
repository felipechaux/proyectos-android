package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestCheckResultLotteryDTO {

    @SerializedName("fechaSorteo")
    var drawDate: String? = null

    @SerializedName("codLoteria")
    var lotteryCode: String? = null

    @SerializedName("canalId")
    var canalId: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null

}