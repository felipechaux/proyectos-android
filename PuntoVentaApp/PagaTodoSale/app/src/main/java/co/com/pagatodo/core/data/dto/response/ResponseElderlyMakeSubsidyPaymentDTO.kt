package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseElderlyMakeSubsidyPaymentDTO  :BaseResponseDTO(){

    @SerializedName("token")
    var token: String? = null

    @SerializedName("codOficina")
    var officeCode: String? = null

    @SerializedName("sitioVenta")
    var salePlacement: String? = null

    @SerializedName("codPuntoVenta")
    var salePlacementCode: String? = null

    @SerializedName("nombreEmpresaPaga")
    var businessName: String? = null

    @SerializedName("nitEmpresaPaga")
    var businessNit: String? = null

    @SerializedName("nombreMunicipio")
    var cityName: String? = null

    @SerializedName("tituloColombiaMayor")
    var title: String? = null

    @SerializedName("numTransaccion")
    var transactionNumber: String? = null



}