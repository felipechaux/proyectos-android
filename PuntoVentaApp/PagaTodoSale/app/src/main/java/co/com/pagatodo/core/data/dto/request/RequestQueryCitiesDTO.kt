package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestQueryCitiesDTO {

    @SerializedName("tipoUsuario")
    var userType: String ? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String ? = null

    @SerializedName("canalId")
    var canalId: String ? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String ? = null

    @SerializedName("nombreCiudad")
    var cityName: String ? = null

    


}