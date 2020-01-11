package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestRegistraduriaCollectionDTO {

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("direccionMac")
    var macAddress: String? = null

    @SerializedName("volEquipo")
    var deviceVol: String? = null

    @SerializedName("canalId")
    var canalId: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null

    @SerializedName("tipoDocumento")
    var documentType: String? = null

    @SerializedName("numDocumento")
    var documentNumber: String? = null

    @SerializedName("primerNombre")
    var firstName: String? = null

    @SerializedName("segundoNombre")
    var secondName: String? = null

    @SerializedName("primerApellido")
    var lastName: String? = null

    @SerializedName("segundoApellido")
    var secondLastName: String? = null

    @SerializedName("particula")
    var particle: String? = null

    @SerializedName("numCelular")
    var movile: String? = null

    @SerializedName("tipoDocTramite")
    var docTypeProcess: String? = null

    @SerializedName("nombreServicio")
    var serviceName: String? = null

    @SerializedName("oficinaRegistraduria")
    var registraduriaOffice: String? = null

    @SerializedName("correo")
    var email: String? = null

    @SerializedName("valor")
    var value: Int? = null

}