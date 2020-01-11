package co.com.pagatodo.core.data.dto.request

import com.google.gson.annotations.SerializedName

class RequestElderlySaveFingerPrintDTO {

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

    @SerializedName("login")
    var login: String? = null

    @SerializedName("numDocumento")
    var document: String? = null

    @SerializedName("tipoDocumento")
    var documentType: String? = null

    //MANO IZQUIERDA

    // INDICE
    @SerializedName("indiceIzquierdo")
    var indexFingerPrintL: String? = null

    //CORAZON
    @SerializedName("medioIzquierdo")
    var middleFingerPrintL: String? = null

    //ANULAR
    @SerializedName("anularIzquierdo")
    var ringFingerPrintL: String? = null

    //MEÑIQUE
    @SerializedName("meniqueIzquierdo")
    var littleFingerPrintL: String? = null

    //MANO DERECHA

    // INDICE
    @SerializedName("indiceDerecho")
    var indexFingerPrintR: String? = null

    //CORAZON
    @SerializedName("medioDerecho")
    var middleFingerPrintR: String? = null

    //ANULAR
    @SerializedName("anularDerecho")
    var ringFingerPrintR: String? = null

    //MEÑIQUE
    @SerializedName("meniqueDerecho")
    var littleFingerPrintR: String? = null

}