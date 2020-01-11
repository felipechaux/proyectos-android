package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.BalotoAddOnGamesDTO
import co.com.pagatodo.core.data.dto.BalotoDrawDTO
import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseBalotoParametersDTO {
    @SerializedName("responseCode")
    var responseCode: String? = null

    @SerializedName("exito")
    var isSuccess: Boolean? = null

    @SerializedName("fechaTransaccion")
    var transactionDate: String? = null

    @SerializedName("horaTransaccion")
    var transactionTime: String? = null

    @SerializedName("idTransaccionSolicitud")
    var transactionId: Int? = null

    @SerializedName("mensaje")
    var message: String? = null

    @SerializedName("mensajes")
    var messages: List<MessageDTO>? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("basePrice")
    var basePrice: Int? = null

    @SerializedName("maxPrice")
    var maxPrice: Int? = null

    @SerializedName("minBoards")
    var minBoards: Int? = null

    @SerializedName("maxBoards")
    var maxBoards: Int? = null

    @SerializedName("quickPickAvailable")
    var quickPickAvailable: Boolean? = null

    @SerializedName("multiplierAvailable")
    var multiplierAvailable: Boolean? = null

    @SerializedName("stakes")
    var stakes: List<Int>? = null

    @SerializedName("durations")
    var durations: List<Int>? = null

    @SerializedName("maxDuration")
    var maxDuration: Int? = null

    @SerializedName("primarySelectionsLowNumber")
    var primarySelectionsLowNumber: Int? = null

    @SerializedName("primarySelectionsHighNumber")
    var primarySelectionsHighNumber: Int? = null

    @SerializedName("secondarySelectionsLowNumber")
    var secondarySelectionsLowNumber: Int? = null

    @SerializedName("secondarySelectionsHighNumber")
    var secondarySelectionsHighNumber: Int? = null

    @SerializedName("gameId")
    var gameId: String? = null

    @SerializedName("revision")
    var revision: String? = null

    @SerializedName("addonGames")
    var addonGames: List<BalotoAddOnGamesDTO>? = null

    @SerializedName("draws")
    var draws: List<BalotoDrawDTO>? = null

    @SerializedName("parametrosProducto")
    var productParameters: ProductParametersDTO? = null

}

class ProductParametersDTO {
    @SerializedName("TIQUETE_HEADER_SP")
    var headerTicketSp: String? = null
    @SerializedName("TIQUETE_FOOTER_SP")
    var footerTicketSp: String? = null
    @SerializedName("TIQUETE_HEADER_PV")
    var headerTicketPv: String? = null
    @SerializedName("TIQUETE_FOOTER_PV")
    var footerTicketPv: String? = null
}