package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class SportingBetDTO {
    @SerializedName("idApuesta")
    var betId: Int? = null

    @SerializedName("codigo")
    var code: String? = null

    @SerializedName("local")
    var local: String? = null

    @SerializedName("localNombre")
    var localName: String? = null

    @SerializedName("visitante")
    var visitor: String? = null

    @SerializedName("visitanteNombre")
    var visitorName: String? = null

    @SerializedName("fecha")
    var date: Long? = null

    @SerializedName("hora")
    var time: String? = null

    @SerializedName("resultadoLocal")
    var isLocalResult: Boolean? = null

    @SerializedName("resultadoVisitante")
    var isVisitorResult: Boolean? = null

    @SerializedName("resultadoEmpate")
    var isTieResult: Boolean? = null

    @SerializedName("marcadorLocal")
    var localMarker: Int? = null

    @SerializedName("marcadorVisitante")
    var visitorMarker: Int? = null
}