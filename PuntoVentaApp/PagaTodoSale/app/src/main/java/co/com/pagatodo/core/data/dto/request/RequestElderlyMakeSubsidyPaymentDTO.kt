package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestElderlyMakeSubsidyPaymentDTO {

    @SerializedName("tipoUsuario")
    var userType: String? = null

    @SerializedName("codigoTerminal")
    var terminalCode: String? = null

    @SerializedName("canalId")
    var canalId: String? = null

    @SerializedName("codigoVendedor")
    var sellerCode: String? = null

    @SerializedName("codigoProducto")
    var productCode: String? = null

    @SerializedName("transactionTime")
    var transactionTime: String? = null

    @SerializedName("login")
    var login: String? = null

    @SerializedName("referencia")
    var reference: String? = null

    @SerializedName("tipoDocTitular")
    var titularDocumentType: String? = null

    @SerializedName("numDocTitular")
    var titularDocumentId: String? = null

    @SerializedName("nombreTitular")
    var titularName: String? = null

    @SerializedName("primerApellidoTitular")
    var titularFirstLastName: String? = null

    @SerializedName("segundoApellidoTitular")
    var titularSecondLastName: String? = null

    @SerializedName("valorPago")
    var payValue: String? = null

    @SerializedName("curador")
    var curator: Boolean? = null

    @SerializedName("tipoDocAutorizado")
    var documentTypeAuthorired: String? = null

    @SerializedName("numDocAutorizado")
    var documentAuthorired: String? = null

    @SerializedName("nombreAutorizado")
    var authorizedName: String? = null

    @SerializedName("primerApellidoAutorizado")
    var authorizedFirstLastName: String? = null

    @SerializedName("segundoApellidoAutorizado")
    var authorizedSecondLastName: String? = null

    @SerializedName("nombreHuella")
    var footprintName: String? = null

    @SerializedName("imagenHuella")
    var footprintImage: String? = null

}