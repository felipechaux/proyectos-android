package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class PromotionDTO {
    @SerializedName("cantidadApuestas")
    var bettingAmount: Int? = null
    @SerializedName("cantidadCifras")
    var quantityFigures: String? = null
    @SerializedName("cantidadLoterias")
    var lotteriesQuantity: Int? = null
    @SerializedName("debeGenerarAleatorios")
    var generateRandom: Boolean = false
    @SerializedName("idPromocion")
    var id: Int? = null
    @SerializedName("nombrePromocion")
    var name: String? = null
    @SerializedName("valorAbierto")
    var openValue: Boolean = false
    @SerializedName("valoresModalidades")
    var modalitiesValues: List<String>? = null
}