package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class PayBillDTO {
    @SerializedName("exito")
    var isSuccess: Boolean? = null

    @SerializedName("lstErrores")
    var lstErrors: String? = null

    @SerializedName("usuario")
    var user: String? = null

    @SerializedName("codCiudad")
    var cityCode: String? = null

    @SerializedName("codOficina")
    var oficeCode: String? = null

    @SerializedName("codVendedor")
    var sellerCode: String? = null

    @SerializedName("fechaHora")
    var dateHour: String? = null

    @SerializedName("nombreServicio")
    var serviceName: String? = null

    @SerializedName("lstFacturas")
    var lstBills: List<BillDTO>? = null
}