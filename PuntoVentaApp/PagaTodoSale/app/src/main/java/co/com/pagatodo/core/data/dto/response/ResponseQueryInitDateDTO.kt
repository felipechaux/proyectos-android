package co.com.pagatodo.core.data.dto.response

import co.com.pagatodo.core.data.dto.MessageDTO
import com.google.gson.annotations.SerializedName

class ResponseQueryInitDateDTO:BaseResponseDTO() {


    @SerializedName("valorTarjeta")
    var cardValue: String? = null

    @SerializedName("directorioLocalSitp")
    var localFolderSitp: String? = null

    @SerializedName("cuentaSitp")
    var sitpAccount: String? = null

    @SerializedName("dispositivoSitp")
    var sitpDevice: String? = null

    @SerializedName("ipSitp")
    var sitpIp: String? = null

    @SerializedName("puertoSitp")
    var sitpPort: String? = null

    @SerializedName("rutadllSitp")
    var sitpDllRoute: String? = null

    @SerializedName("tipoTarjetaNoInventario")
    var noInventoryCardType: String? = null
}