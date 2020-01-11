package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.BillRequestDTO
import com.google.gson.annotations.SerializedName

open class RequestBbvaValidateAndPayBillDTO {

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

    @SerializedName("valor")
    var value: Int = 0

    @SerializedName("factura")
    var bill: BillRequestDTO? = null

}
