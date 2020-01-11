package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.PinDTO
import co.com.pagatodo.core.data.dto.request.RequestQueryPinWalletDTO
import co.com.pagatodo.core.data.dto.request.RequestVirtualWalletActivatePinDTO
import co.com.pagatodo.core.data.model.KeyValueModel
import co.com.pagatodo.core.data.model.PinModel
import co.com.pagatodo.core.data.model.print.VirtualWalletPrintModel
import co.com.pagatodo.core.data.model.request.RequestActivatePinModel
import co.com.pagatodo.core.data.model.response.ResponseActivatePinModel
import co.com.pagatodo.core.data.model.response.ResponseVirtualWalletQueryPinModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.repositories.IVirtualWalletRepository
import co.com.pagatodo.core.di.DaggerVirtualWalletComponent
import co.com.pagatodo.core.di.VirtualWalletModule
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

interface IVitualWalletInteractor {
    fun getProductParameters(): Observable<List<KeyValueModel>>?
    fun queryPin(pin: String): Observable<ResponseVirtualWalletQueryPinModel>?
    fun activatePin(requestActivatePin: RequestActivatePinModel, isRetry: Boolean = true, transactionType: String? = null): Observable<ResponseActivatePinModel>?
    fun print(printModel: VirtualWalletPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?)
}

@Singleton
class VitualWalletInteractor: IVitualWalletInteractor {

    @Inject
    lateinit var repository: IVirtualWalletRepository

    init {
        DaggerVirtualWalletComponent.builder()
            .virtualWalletModule(VirtualWalletModule())
            .build().inject(this)
    }

    override fun getProductParameters(): Observable<List<KeyValueModel>>? {
        return repository.getProductParameters()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = arrayListOf<KeyValueModel>()
                it.forEach { ent ->
                    val model = KeyValueModel().apply {
                        key = ent?.key
                        value = ent?.value
                    }
                    response.add(model)
                }
                Observable.just(response)
            }
    }

    override fun queryPin(pin: String): Observable<ResponseVirtualWalletQueryPinModel>? {
        val request = RequestQueryPinWalletDTO().apply {
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            userType = getPreference(R_string(R.string.shared_key_user_type))
            productCode = R_string(R.string.code_product_virtual_wallet)
            this.pin = PinDTO().apply {
                this.pin = pin
                this.pinType = R_string(R.string.pin_type)
            }
        }
        return repository.queryPin(request)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = ResponseVirtualWalletQueryPinModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionHour
                    message = it.message
                    this.pin = PinModel().apply {
                        this.pin = it.pin?.pin
                        value = it.pin?.value
                        pinType = it.pin?.pinType
                    }
                }
                Observable.just(response)
            }
    }

    override fun activatePin(requestActivatePin: RequestActivatePinModel, isRetry: Boolean, transactionType: String?): Observable<ResponseActivatePinModel>? {
        val request = RequestVirtualWalletActivatePinDTO().apply {
            serie1 = getPreference(R_string(R.string.shared_key_current_serie1))
            serie2 = getPreference(R_string(R.string.shared_key_current_serie2))
            value = requestActivatePin.value
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            userType = getPreference(R_string(R.string.shared_key_user_type))
            productCode = R_string(R.string.code_product_virtual_wallet)
            this.pin = PinDTO().apply {
                this.pin = requestActivatePin.pin
                this.pinType = R_string(R.string.pin_type)
                this.value = requestActivatePin.value
            }
            this.transactionTime = requestActivatePin.transactionTime
            this.sequenceTransaction = requestActivatePin.sequenceTransaction
        }

        return repository.activatePin(request, isRetry, transactionType)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = ResponseActivatePinModel().apply{
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionHour = it.transactionHour
                    transactionDate = it.transactionDate
                    transactionResponseId = it.transactionId
                    message = it.message
                    this.pin = PinModel().apply {
                        this.pin = it.pin?.pin
                        pinType = it.pin?.pinType
                        this.value = it.pin?.value
                    }
                    serie1 = it.serie1
                    serie2 = it.serie2
                    currentSerie2 = it.currentSerie2
                }
                Observable.just(response)
            }
    }

    override fun print(printModel: VirtualWalletPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?) {
        repository.print(printModel, callback)
    }
}