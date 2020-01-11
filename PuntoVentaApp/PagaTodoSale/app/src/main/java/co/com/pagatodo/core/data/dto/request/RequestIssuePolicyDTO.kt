package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestIssuePolicyDTO {
    @SerializedName("valorRecaudo")
    var collectingValue: String? = null
    @SerializedName("placa")
    var licensePlate: String? = null
    @SerializedName("marca")
    var brand: String? = null
    @SerializedName("tipoDocumentoTomador")
    var takerDocumentType: String? = null
    @SerializedName("tipoPago")
    var paymentType: String? = null
    @SerializedName("numCelular")
    var phone: String? = null
    @SerializedName("canalId")
    var channelId: String? = null
    @SerializedName("codigoProducto")
    var productCode: String? = null
    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("idTransaccionProducto")
    var transactionId: String? = null

    @SerializedName("lstMediosPago")
    var paymentMethod: List<PaymentMethodDTO>? = null

    @SerializedName("codigoTerminal")
    var terminal: String? = null
    @SerializedName("tipoUsuario")
    var userType: String? = null
    @SerializedName("codEntidad")
    var entityCode: String? = null

}

class PaymentMethodDTO {
    @SerializedName("idMedioPagoEnum")
    var paymentMethod: String? = null
}