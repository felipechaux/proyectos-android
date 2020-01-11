package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class OperatorDTO {
    @SerializedName("operador")
    var operator: String? = null
    @SerializedName("nombreOperador")
    var operatorName: String? = null
    @SerializedName("codigoOperador")
    var operatorCode: String? = null
    @SerializedName("codigoOperadorPaquete")
    var operatorCodePackage: String? = null
    @SerializedName("paquetes")
    var packages: List<PackageDTO>? = null
    @SerializedName("valorMinimo")
    var minValue: Int? = null
    @SerializedName("valorMaximo")
    var maxValue: Int? = null
    @SerializedName("minDigitos")
    var minDigits: Int? = null
    @SerializedName("maxDigitos")
    var maxDigits: Int? = null
    @SerializedName("multiplo")
    var multipleDigits: Int? = null
}