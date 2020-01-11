package co.com.pagatodo.core.data.dto.response

import com.google.gson.annotations.SerializedName

class ResponseBetPlayCheckOutDTO :BaseResponseDTO() {

    @SerializedName("codigoMuncipio")
    var cityCode: String? = null

    @SerializedName("codigoOficina")
    var OfficeCode:String? = null

    @SerializedName("codigoPuntoVenta")
    var salepointCode:String? = null

    @SerializedName("codigoArriendo")
    var rentCode:String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("volEquipo")
    var teamVol: String? = null

    @SerializedName("token")
    var token: String? = null

    @SerializedName("usuario")
    var user: String? = null

    @SerializedName("idTransaccionProducto")
    var transactionProduct: Int? = null

    @SerializedName("milisegRta")
    var milisegRta: Int? = null

    @SerializedName("tramaRta")
    var tramaRta: String? = null

    @SerializedName("numDocumento")
    var numDocumented: String? = null

    @SerializedName("valor")
    var value: String? = null

    @SerializedName("saldo")
    var balance: String? = null

    @SerializedName("codigoSeguridad")
    var securityCode: String? = null

    @SerializedName("reimpresion")
    var isRePrint: Boolean? = null

    @SerializedName("serie1")
    var serie1: String? = null

    @SerializedName("serie2")
    var serie2: String? = null

    @SerializedName("serie2Actual")
    var currentSerie2: String? = null

}