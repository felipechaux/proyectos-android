package co.com.pagatodo.core.data.dto

import co.com.pagatodo.core.data.dto.response.ResponseAlcanosDTO
import com.google.gson.annotations.SerializedName

class BillDTO {
    @SerializedName("idServicio")
    var serviceId: String? = null

    @SerializedName("referenciaPago")
    var payReference: String? = null

    @SerializedName("codBarras")
    var barCode: String? = null

    @SerializedName("fechaVencimiento")
    var expirationDate: String? = null

    @SerializedName("valorFactura")
    var billValue: String? = null

    @SerializedName("valorPagar")
    var valueToPay: String? = null

    @SerializedName("facturaVencida")
    var isBillExpirated: Boolean? = null

    @SerializedName("nombreUsuario")
    var userName: String? = null

    @SerializedName("numeroFactura")
    var billNumber: String? = null

    @SerializedName("descripcion")
    var description: String? = null

    @SerializedName("estadoFactura")
    var billStatus: String? = null

    @SerializedName("numeroCuenta")
    var accountNumber: String? = null

    @SerializedName("idEmpresa")
    var companyId: String? = null

    @SerializedName("nitEmpresa")
    var companyNit: String? = null

    @SerializedName("pkCodigoSeqFactura")
    var codeSeqBill: String? = null

    @SerializedName("fechaRecaudo")
    var collectionDate: String? = null

    @SerializedName("horaRecaudo")
    var collectionHour: String? = null

    @SerializedName("numeroComprobante")
    var voucherNumber: String? = null

    @SerializedName("direccionMac")
    var macAddress: String? = null

    @SerializedName("volEquipo")
    var teamVol: String? = null

    @SerializedName("codPtoVenta")
    var codePointSale: String? = null

    @SerializedName("numDocVendedor")
    var sellerNumberDocument: String? = null

    @SerializedName("terminal")
    var terminal: String? = null

    @SerializedName("nombreServicio")
    var nameService: String? = null

    @SerializedName("idFacturaPlano")
    var flatIdBill: String? = null

    @SerializedName("facturaServicioDTO")
    var billService: BillServiceDTO? = null

    @SerializedName("respuestaAlcanosDTO")
    var alcanos: ResponseAlcanosDTO? = null
}