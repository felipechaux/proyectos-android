package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponsePointedFingersDTO:BaseResponseDTO() {

    //MANO IZQUIERDA

    // INDICE
    @SerializedName("indiceIzquierdo")
    var indexFingerL: String? = null

    //CORAZON
    @SerializedName("medioIzquierdo")
    var middleFingerL: String? = null

    //ANULAR
    @SerializedName("anularIzquierdo")
    var ringFingerL: String? = null

    //MEÑIQUE
    @SerializedName("meniqueIzquierdo")
    var littleFingerL: String? = null

    //MANO DERECHA

    // INDICE
    @SerializedName("indiceDerecho")
    var indexFingerR: String? = null

    //CORAZON
    @SerializedName("medioDerecho")
    var middleFingerR: String? = null

    //ANULAR
    @SerializedName("anularDerecho")
    var ringFingerR: String? = null

    //MEÑIQUE
    @SerializedName("meniqueDerecho")
    var littleFingerR: String? = null

}