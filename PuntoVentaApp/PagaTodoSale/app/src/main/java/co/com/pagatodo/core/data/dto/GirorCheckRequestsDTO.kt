package co.com.pagatodo.core.data.dto

import com.google.gson.annotations.SerializedName

class GirorCheckRequestsDTO {

    @SerializedName("idSolicitud")
    var idRequest: Int? = null

    @SerializedName("agencia")
    var agency: String? = null

    @SerializedName("codCaja")
    var boxCode: String? = null

    @SerializedName("nota")
    var note: String? = null

    @SerializedName("referencia")
    var reference: String? = null

    @SerializedName("fecha")
    var date: String? = null

    @SerializedName("hora")
    var hour: String? = null

    @SerializedName("volumen")
    var vol: String? = null

    @SerializedName("beneficiarioIdentificacion")
    var beneficiaryId: String? = null

    @SerializedName("beneficiarioTipoDocumento")
    var beneficiaryIdType: String? = null

    @SerializedName("beneficiarioNombre")
    var beneficiaryName: String? = null

    @SerializedName("observacion")
    var observation: String? = null

    @SerializedName("codTipoSolicitud")
    var requestType: String? = null

    @SerializedName("nombTipoSolicitud")
    var requestName: String? = null

    @SerializedName("codTipoMensaje")
    var messageTypeCode: String? = null

    @SerializedName("nombTipoMensaje")
    var messageTypeName: String? = null

    @SerializedName("estado")
    var status: String? = null

    @Transient
    var isPrinter: Boolean? = null

}