package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestBbvaDTO {

    @SerializedName("canalId")
    var channelId: String? = null

    @SerializedName("volEquipo")
    var volMachine: String? = null

    @SerializedName("direccionMac")
    var macAdress: String? = null

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("concepto")
    var concept: String? = null

    @SerializedName("descProducto")
    var descProduc: String? = null

    @SerializedName("loginCliente")
    var loginClient: String? = null

    @SerializedName("numRecibo")
    var numReceipt: String? = null

    @SerializedName("rrn")
    var rrn: String? = null

    @SerializedName("tipoTransaccionAsesor")
    var typeTransactionAdvisor: String? = null

    @SerializedName("valor")
    var value: String? = null

}