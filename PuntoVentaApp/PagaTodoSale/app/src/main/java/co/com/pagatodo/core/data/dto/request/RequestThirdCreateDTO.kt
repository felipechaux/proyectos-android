package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.GiroUserDTO
import co.com.pagatodo.core.data.dto.ThirdDTO
import com.google.gson.annotations.SerializedName

class RequestThirdCreateDTO {

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

    @SerializedName("empleaBiometria")
    var biometrics: String? = null

    @SerializedName("usuario")
    var user: GiroUserDTO? = null

    @SerializedName("tercero")
    var third: ThirdDTO? = null

    @SerializedName("huellaIzquierda")
    var fingerPrintL: String? = null

    @SerializedName("huellaDerecha")
    var fingerPrintR: String? = null

    @SerializedName("pago")
    var isPay: Boolean? = null





}