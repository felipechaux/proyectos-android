package co.com.pagatodo.core.data.dto.request

import co.com.pagatodo.core.data.dto.BaseRequestDTO
import co.com.pagatodo.core.data.dto.GiroCaptureBoxDTO
import co.com.pagatodo.core.data.dto.GiroUserDTO
import com.google.gson.annotations.SerializedName

open class RequestGiroBase : BaseRequestDTO(){

    @SerializedName("codigoProducto")
    var productCode: String? = null

    @SerializedName("usuario")
    var user: GiroUserDTO? = null

    @SerializedName("caja")
    var box: GiroCaptureBoxDTO? = null



}