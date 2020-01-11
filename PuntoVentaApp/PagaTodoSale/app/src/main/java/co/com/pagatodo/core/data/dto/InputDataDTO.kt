package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class InputDataDTO {

    @SerializedName("idTipoDocumento")
    var documentTypeID: String? = null

    @SerializedName("numDocumento")
    var documentNumber: String? = null

    @SerializedName("placa")
    var licensePlate: String? = null

    @SerializedName("tipoVehiculo")
    var vehicleType: String? = null

    @SerializedName("codCajero")
    var cashierCode: String? = null

    @SerializedName("codDane")
    var daneCode: String? = null

    @SerializedName("numCelular")
    var phone: String? = null

    @SerializedName("reImprimir")
    var rePrint: Boolean? = false
}