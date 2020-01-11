package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class ThirdCityDTO {

    @SerializedName("idCiudad")
    var idCity : String?=null

    @SerializedName("nombreCiudad")
    var cityName : String?=null

    @SerializedName("idDepartamento")
    var idDepartment : String?=null

    @SerializedName("ciudadDepto")
    var cityDepto : String?=null

    @SerializedName("codigoDane")
    var codeDane : String?=null

    @SerializedName("codigoSise")
    var codeSise : String?=null

    @SerializedName("nombreDepartamento")
    var DepartamentName : String?=null


}