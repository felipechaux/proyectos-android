package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.InputDataDTO
import com.google.gson.annotations.SerializedName

class RequestQueryVirtualSoatDTO {
    @SerializedName("direccionMac")
    var mac: String? = null
    @SerializedName("canalId")
    var channelId: String? = null
    @SerializedName("codigoTerminal")
    var terminalCode: String? = null
    @SerializedName("codigoProducto")
    var productCode: String? = null
    @SerializedName("codigoVendedor")
    var sellerCode: String? = null
    @SerializedName("codEntidad")
    var entityCode: String? = null
    @SerializedName("tipoUsuario")
    var userType: String? = null
    @SerializedName("datosEntrada")
    var inputData: InputDataDTO? = null
    @SerializedName("datosTransaccion")
    var transactionData: TransactionDataDTO? = null

}

class ClientDataDTO {
    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("userName")
    var userName: String? = null

    @SerializedName("login")
    var login: String? = null

    @SerializedName("terminal")
    var terminal: String? = null

}

class TransactionDataDTO {
    @SerializedName("datosCliente")
    var clientData: ClientDataDTO? = null

    @SerializedName("fecha")
    var date: String? = null

    @SerializedName("id")
    var id: String? = null

}