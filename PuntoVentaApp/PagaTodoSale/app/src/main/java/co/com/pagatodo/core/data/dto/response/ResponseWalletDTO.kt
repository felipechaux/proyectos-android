package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class ResponseWalletDTO {
    @SerializedName("codigoRespuesta")
    var codeResponse: String? = null
    @SerializedName("exito")
    var success: Boolean = false
    @SerializedName("fechaTransaccion")
    var transactionDate: String? = null
    @SerializedName("horaTransaccion")
    var transactionHour: String? = null
    @SerializedName("nombreVendedor")
    var sellerName: String? = null
    @SerializedName("saldoZona")
    var balanceArea: Int = 0
    @SerializedName("ventaZona")
    var sellingArea: Int = 0
    @SerializedName("recaudoZona")
    var recaudoArea: Int = 0
    @SerializedName("ventaVendedor")
    var sellerSale: Int = 0
    @SerializedName("recaudoVendedor")
    var sellerRecaudo: Int = 0
    @SerializedName("saldoVendedor")
    var sellerBalance: Int = 0
    @SerializedName("zona")
    var area: String? = null
}