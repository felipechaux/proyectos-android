package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.IssuePolicyDTO
import com.google.gson.annotations.SerializedName

class ResponseIssuePolicyDTO :BaseResponseDTO(){


    @SerializedName("respuestaEmitirPolizaDTO")
    var issuePolicy: IssuePolicyDTO? = null

    @SerializedName("fechaHoraInicioVigencia")
    var beginValidityDateHour: String? = null
    @SerializedName("fechaHoraFinVigencia")
    var ensValidityDateHour: String? = null
    @SerializedName("numPoliza")
    var policyNumber: String? = null
    @SerializedName("primaSoat")
    var soatValue: String? = null
    @SerializedName("contribucionFosyga")
    var fosygaValue: String? = null
    @SerializedName("tarifaRunt")
    var runtValue: String? = null
    @SerializedName("totalPagar")
    var toalValue: String? = null
    @SerializedName("apellidoNombreTomador")
    var takerNameLast: String? = null
    @SerializedName("numDocumentoTomador")
    var takerDocumentNumber: String? = null
    @SerializedName("ciudadResidenciaTomador")
    var takerCity: String? = null
    @SerializedName("claseVehiculo")
    var vehicleType: String? = null
    @SerializedName("servicio")
    var service: String? = null
    @SerializedName("cilindraje")
    var cylinderCapacity: String? = null
    @SerializedName("model")
    var vehicleModel: String? = null
    @SerializedName("placa")
    var licensePlate: String? = null
    @SerializedName("linea")
    var vehicleLine: String? = null
    @SerializedName("numMotor")
    var motorNumber: String? = null
    @SerializedName("numChasis")
    var chassisNumber: String? = null
    @SerializedName("numVIN")
    var vinNumber: String? = null
    @SerializedName("pasajeros")
    var passengers: String? = null
    @SerializedName("capacidad")
    var capacity: String? = null

}