package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class GeneralProductDTO {
    @SerializedName("codigoProducto")
    var code: String? = null
    @SerializedName("nombreProducto")
    var name: String? = null
    @SerializedName("loterias")
    var lotteries: List<LotteryDTO>? = null
    @SerializedName("modalidades")
    var modalities: List<ModalityDTO>? = null
    @SerializedName("promocionales")
    var promotional: List<PromotionDTO>? = null
    @SerializedName("parametrosProducto")
    var productParameters: List<ParameterDTO>? = null
    @SerializedName("rifas")
    var raffles: List<RaffleDTO>? = null
}