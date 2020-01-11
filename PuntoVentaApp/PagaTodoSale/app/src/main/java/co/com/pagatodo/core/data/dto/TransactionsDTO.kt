package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class TransactionsDTO {
    @SerializedName("codigoRespuesta")
var responseCode: String? = null
    @SerializedName("exito")
    var isSuccess: Boolean? = false
    @SerializedName("fechaTransaccion")
    var transactionDate: String? = null
    @SerializedName("horaTransaccion")
    var transactionHour: String? = null
    @SerializedName("mensajes")
    var messages: List<MessageDTO>? = null
    @SerializedName("count")
    var count: Boolean? = false
    @SerializedName("codPtoVenta")
    var salepointCode: String? = null
    @SerializedName("idTranssaccionProducto")
    var prodcutTransactionId: Int? = null
    @SerializedName("synNromov")
    var synNromov: Int? = null
    @SerializedName("estado")
    var state: String? = null
    @SerializedName("tipoOperacion")
    var operationType: String? = null
    @SerializedName("usario")
    var userName: String? = null
    @SerializedName("terminal")
    var terminalCode: String? = null
    @SerializedName("volumen")
    var vol: String? = null
    @SerializedName("codArriendo")
    var rentingCode: String? = null
    @SerializedName("codOficina")
    var officeCode: String? = null
    @SerializedName("referenciaPago")
    var reference: String? = null
    @SerializedName("sitioVenta")
    var salepointPlace: String? = null
    @SerializedName("valorFactura")
    var billValue: Int? = null
    @SerializedName("mensajeRespuesta")
    var responseMessage: String? = null
    @SerializedName("fechaEfectiva")
    var effectiveDate: String? = null
    @SerializedName("codTransaccionPrisma")
    var prismaTransactionCode: String? = null
    @SerializedName("cantidadMovimientos")
    var transactionQuantity: Int? = null

    @SerializedName("nombrePagador")
    var userNamePay: String? = null

    @SerializedName("documentoPagador")
    var documentUserPay: String? = null

}