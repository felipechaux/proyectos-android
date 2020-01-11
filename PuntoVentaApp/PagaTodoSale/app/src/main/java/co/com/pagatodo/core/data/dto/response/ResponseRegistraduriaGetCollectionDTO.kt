package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseRegistraduriaGetCollectionDTO :BaseResponseDTO(){

    @SerializedName("synNromov")
    var synNromov: Long? = null

    @SerializedName("codPtoVenta")
    var salePointCode: String? = null

    @SerializedName("codOficina")
    var officeCode: String? = null

    @SerializedName("fechaTransaccionProducto")
    var transactionProductDate: String? = null

    @SerializedName("horaTransaccionProducto")
    var transactionProductTime: String? = null

    @SerializedName("estado")
    var status: String? = null

    @SerializedName("usuario")
    var user: String? = null

    @SerializedName("caja")
    var box: String? = null

    @SerializedName("terminal")
    var terminal: String? = null

    @SerializedName("volumen")
    var volume: String? = null

    @SerializedName("tipoOperacion")
    var operationType: String? = null

    @SerializedName("sitioVenta")
    var salePlace: String? = null

    @SerializedName("tipoDocumento")
    var documentType: String? = null

    @SerializedName("numDocumento")
    var documentNumber: String? = null

    @SerializedName("primerNombre")
    var firstName: String? = null

    @SerializedName("primerApellido")
    var firstLastName: String? = null

    @SerializedName("oficinaRegistraduria")
    var registraduriaOffice: String? = null

    @SerializedName("numCelular")
    var cellphone: String? = null

    @SerializedName("tipoDocTramite")
    var procedureDocumentType: String? = null

    @SerializedName("nombreServicio")
    var serviceName: String? = null

    @SerializedName("valor")
    var value: Long? = null

    @SerializedName("codTransaccionPrisma")
    var transactionPrismaCode: String? = null

    @SerializedName("msgServicio")
    var serviceMessage: String? = null

    @SerializedName("nombreEmpresa")
    var nameCompany: String? = null

    @SerializedName("nitEmpresa")
    var nitCompany: String? = null

    @SerializedName("txtPiePagina")
    var pageFooter: String? = null


}