package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestRaffleAvailableRangeDTO {


    @SerializedName("canalId")
    var channelId: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null

    @SerializedName("codigoRifa")
    var raffleCode: String? = null

    @SerializedName("limiteInicio")
    var startNum: String? = null

    @SerializedName("limiteFinal")
    var endNum: String? = null




}