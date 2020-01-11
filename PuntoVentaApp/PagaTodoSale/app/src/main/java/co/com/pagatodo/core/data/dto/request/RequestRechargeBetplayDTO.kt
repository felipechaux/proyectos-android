package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestRechargeBetplayDTO {
    @SerializedName("numDocumento")
    var documentNumber: String? = null
    @SerializedName("reimpresion")
    var reprint: Boolean = false
    @SerializedName("direccionMac")
    var macAddress: String? = null
    @SerializedName("volEquipo")
    var equipmentVolume: String? = null
    @SerializedName("codigoVendedor")
    var sellerCode: String? = null
    @SerializedName("codigoProducto")
    var productCode: String? = null
    @SerializedName("serie1")
    var serie1: String? = null
    @SerializedName("serie2")
    var serie2: String? = null
    @SerializedName("valor")
    var value: Double? = null
    @SerializedName("canalId")
    var canalId: String? = null
    @SerializedName("codigoTerminal")
    var terminalCode: String? = null
    @SerializedName("tipoUsuario")
    var userType: String? = null
    @SerializedName("tipoTransaccionSolicitud")
    var transactionType: String? = null
    @SerializedName("transactionTime")
    var transactionTime: Long? = null
    @SerializedName("secuenciaTransaccion")
    var sequenceTransaction: Int? = null
}