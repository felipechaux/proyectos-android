package co.com.pagatodo.core.views.sitp.ClienteRecaudoSitp

import android.content.Context
import apirecargaandroidjni.rbsas.com.proxyapi.ApiRecargaAndroid
import co.com.pagatodo.core.data.dto.response.ResponseQueryInitDateDTO
import co.com.pagatodo.core.views.sitp.ClienteRecaudoSitp.constants.ECodigosError

class ClientSitpFacade(context: Context) {

    private lateinit var api: ApiRecargaAndroid
    private var handlerFiles: HandlerFiles? = null
    private lateinit var context: Context
    private var lastError: Int = 0
    private lateinit var instance: ClientSitpFacade

    init {
        this.context = context
        api = ApiRecargaAndroid(context)
        handlerFiles = HandlerFiles(context, api)
    }

    fun startService() {
        api.startService()
    }

    fun stopService() {
        api.stop()
    }

    fun createDirectory(data: ResponseQueryInitDateDTO) {
        handlerFiles?.createDirectory(data)
    }

    fun init(): Boolean {
        return handlerFiles?.init()!!
    }

    fun getLastError(): Int {
        lastError = api.error
        return lastError
    }

    fun getDescriptionLastError(): ECodigosError? {
        return ECodigosError.valueOf(lastError)
    }

    fun getCardId(): Int {
        return api.cardID
    }

    fun getLastCardID(): String {
        return api.lastCardID()
    }

    fun getCardBalance(): Int {
        return api.cardBalance()
    }

    fun getSAMBalance(): Int {
        return  api.samBalance()
    }

    fun getCardRecharge(amount: Int): Int {
        return api.recharge(amount)
    }

    fun getValue(): Int {
        return api.getvalue()
    }

}

