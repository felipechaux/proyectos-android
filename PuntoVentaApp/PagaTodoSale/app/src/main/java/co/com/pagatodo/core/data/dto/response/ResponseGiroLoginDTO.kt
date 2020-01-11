package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.*
import com.google.gson.annotations.SerializedName

open class ResponseGiroLoginDTO :BaseResponseDTO(){



    @SerializedName("usuario")
    var user: ResponseGiroLoginUserDTO? = null

    @SerializedName("empresa")
    var company: ResponseGiroLoginCompanyDTO? = null

    @SerializedName("caja")
    var box: ResponseGiroLoginBoxDTO? = null

    @SerializedName("idMoneda")
    var currencyId: String? = null

    @SerializedName("codMoneda")
    var currencyCode: String? = null

    @SerializedName("descripcionMoneda")
    var currencyDescription: String? = null

    @SerializedName("vrgDocuprodDocumId")
    var vrgDocuprodDocumId: String? = null

    @SerializedName("vrgDocuprodProduId")
    var vrgDocuprodProduId: String? = null

    @SerializedName("vrgMedipagoCodigo")
    var vrgMedipagoCode: String? = null

    @SerializedName("vrgMedipagoDescripcion")
    var vrgMedipagoDescription: String? = null

    @SerializedName("vrgContratoSpn")
    var vrgContractSpn: String? = null

    @SerializedName("empleaBiometria")
    var employsBiometrics: String? = null

    @SerializedName("cadenaImpresionCaptacion")
    var chainImpressionCaptation: String? = null

    @SerializedName("cadenaImpresionPago")
    var stringPrintPayment: String? = null

    @Transient
    var isload: Boolean? = null


}