package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseElderlyGetReprintDTO :BaseResponseDTO() {



    @SerializedName("token")
    var token: String? = null

    @SerializedName("nombreEmpresa")
    var businessName: String? = null

    @SerializedName("nitEmpresa")
    var businessNit: String? = null

    @SerializedName("titulo")
    var title: String? = null

    @SerializedName("numDocumento")
    var documentNumber: String? = null

    @SerializedName("codOficina")
    var officeCode: String? = null

    @SerializedName("sitioVenta")
    var salePlace: String? = null

    @SerializedName("nombreMunicipio")
    var cityName: String? = null

    @SerializedName("fechaTransaccionProducto")
    var transactionProductDate: String? = null

    @SerializedName("horaTransaccionProducto")
    var transactionProductTime: String? = null

    @SerializedName("nombre")
    var name: String? = null

    @SerializedName("primerApellido")
    var firstLastName: String? = null

    @SerializedName("segundoApellido")
    var secondLastName: String? = null

    @SerializedName("num_doc_autorizado")
    var docNumberAuthorized: String? = null

    @SerializedName("valorSubsidio")
    var subsidioValue: String? = null

    @SerializedName("usuario")
    var user: String? = null

    @SerializedName("terminal")
    var terminal: String? = null

}