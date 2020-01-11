package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.SportingProductDTO
import com.google.gson.annotations.SerializedName

class ResponseSportingsDTO {
    @SerializedName("serie1")
    var serie1: String? = null

    @SerializedName("serie2")
    var serie2: String? = null

    @SerializedName("idProductoExterno")
    var idExternalProduct: Int? = null

    @SerializedName("resolucion")
    var resolution: String? = null

    @SerializedName("nombreProducto")
    var productName: String? = null

    @SerializedName("version")
    var version: Int? = null

    @SerializedName("productos")
    var products: List<SportingProductDTO>? = null

    @SerializedName("planPremio")
    var prizePlan: Int? = null

    @SerializedName("maxApuestasMegaGol")
    var megaGoalMaxBet: Int? = null
}