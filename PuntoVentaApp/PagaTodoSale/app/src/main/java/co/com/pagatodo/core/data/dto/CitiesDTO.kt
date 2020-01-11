package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class CitiesDTO {

    @SerializedName("nombreCiudad")
    var cityName: String? = null

    @SerializedName("codigoDane")
    var daneCodigo: String? = null

    @SerializedName("codigoSise")
    var siseCode: String? = null

    @SerializedName("nombreDepartamento")
    var departamentName: String? = null


}