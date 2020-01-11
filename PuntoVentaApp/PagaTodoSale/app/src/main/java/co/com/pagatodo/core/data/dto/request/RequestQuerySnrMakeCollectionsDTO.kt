package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.CollectionSnrDTO
import com.google.gson.annotations.SerializedName

class RequestQuerySnrMakeCollectionsDTO {

    @SerializedName("direccionMac")
    var macAddress: String? = null
    @SerializedName("canalId")
    var channelId: String? = null
    @SerializedName("codigoTerminal")
    var terminalCode: String? = null
    @SerializedName("codigoProducto")
    var productCode: String? = null
    @SerializedName("codigoVendedor")
    var sellerCode: String? = null
    @SerializedName("volEquipo")
    var kitVol: String? = null
    @SerializedName("tipoUsuario")
    var userType: String? = null
    @SerializedName("login")
    var login: String? = null
    @SerializedName("recaudo")
    var collection: CollectionSnrDTO? = null
    @SerializedName("transactionTime")
    var transactionTime: String? = null
    @SerializedName("tipoTransaccionSolicitud")
    var transactionTypeRequest: String? = null

}