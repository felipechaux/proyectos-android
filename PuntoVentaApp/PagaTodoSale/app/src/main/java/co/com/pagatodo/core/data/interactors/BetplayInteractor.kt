package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.request.*
import co.com.pagatodo.core.data.dto.response.ResponseBetPlayCheckOutDTO
import co.com.pagatodo.core.data.model.KeyValueModel
import co.com.pagatodo.core.data.model.response.ResponseBetplayRechargeModel
import co.com.pagatodo.core.data.model.response.ResponsePinQuickBetModel
import co.com.pagatodo.core.data.model.response.ResponseRechargeModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.repositories.IBetplayRepository
import co.com.pagatodo.core.di.BetplayModule
import co.com.pagatodo.core.di.DaggerBetplayComponent
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.StorageUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BetplayInteractor {

    @Inject
    lateinit var betplayRepository: IBetplayRepository

    init {
        DaggerBetplayComponent.builder().betplayModule(BetplayModule()).build().inject(this)
    }

    fun payRecharge(
        documentNumber: String,
        value: Double,
        isRetry: Boolean = true,
        transactionType: String? = null,
        transactionTime: Long?,
        sequenceTransaction: Int?
    ): Observable<ResponseBetplayRechargeModel>? {
        val requestRechargeDTO = createRequestRechargeBetplayDTO(
            documentNumber,
            value,
            transactionTime,
            sequenceTransaction
        )
        return betplayRepository
            .payRecharge(requestRechargeDTO, isRetry, transactionType)
            ?.flatMap {
                val model = ResponseBetplayRechargeModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionHour
                    transactionResponseId = it.transactionId
                    user = it.user
                    serie1 = it.serie1
                    serie2 = it.serie2
                    milisegRta = it.milisegRta
                    tramaRta = it.tramaRta
                    this.documentNumber = it.documentNumber
                    balance = it.balance
                    securityCode = it.securityCode
                    currentSerie2 = it.currentSerie2
                    isRePrint = it.isRePrint

                }
                Observable.just(model)
            }
    }

    fun isDocumentValid(documentNumber: String): Observable<ResponseRechargeModel>? {
        val requestRechargeDTO = createRequestRechargeBetplayDTO(documentNumber, null)
        return betplayRepository
            .isDocumentValid(requestRechargeDTO)
            ?.flatMap {
                val model = ResponseRechargeModel().apply {
                    responseCode = it.responseCode
                    success = it.success
                    transactionAnswerId = it.transactionAnswerId
                    message = it.message
                }
                Observable.just(model)
            }
    }

    private fun createRequestRechargeBetplayDTO(
        documentNumber: String,
        value: Double?,
        transactionTime: Long? = DateUtil.getCurrentDateInUnix(),
        sequenceTransaction: Int? = StorageUtil.getSequenceTransaction()
    ): RequestRechargeBetplayDTO {
        return RequestRechargeBetplayDTO().apply {
            this.documentNumber = documentNumber
            this.value = value
            userType = getPreference(R_string(R.string.shared_key_user_type))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            canalId = getPreference(R_string(R.string.shared_key_canal_id))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            productCode = getPreference(R_string(R.string.code_product_betplay))
            serie1 = getPreference(R_string(R.string.shared_key_current_serie1))
            serie2 = getPreference(R_string(R.string.shared_key_current_serie2))
            macAddress = getPreference(R_string(R.string.shared_key_terminal_code))
            equipmentVolume = getPreference(R_string(R.string.shared_key_terminal_code))
            reprint = false
            this.transactionTime = transactionTime
            this.sequenceTransaction = sequenceTransaction
        }
    }

    fun getProductParametersForBetplay(): Observable<List<KeyValueModel>>? {

        return betplayRepository
            .getProductParametersForBetplayRoom()
            ?.flatMap {
                val response = mutableListOf<KeyValueModel>()
                it.forEach { ent ->
                    val model = KeyValueModel().apply {
                        key = ent.key
                        value = ent.value
                    }
                    response.add(model)
                }
                Observable.just(response)
            }
    }

    fun print(
        numberDoc: String,
        value: Int,
        stubs: String,
        digitChecking: String,
        isRetry: Boolean,
        typePrintText: String,
        isReprint: Boolean,
        isCollect: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        betplayRepository.print(
            numberDoc,
            value,
            stubs,
            digitChecking,
            isRetry,
            typePrintText,
            isReprint,
            isCollect,
            callback
        )
    }

    fun reprintBetplay(): Observable<ResponseBetplayRechargeModel>? {
        val request = RequestBetplayReprintDTO().apply {
            userType = getPreference(R_string(R.string.shared_key_user_type))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            productCode = getPreference(R_string(R.string.code_product_betplay))
            macDirection = getPreference(R_string(R.string.shared_key_terminal_code))
            teamVol = getPreference(R_string(R.string.shared_key_terminal_code))
            serie1 = getPreference(R_string(R.string.shared_key_current_serie1))
            serie2 = getPreference(R_string(R.string.shared_key_current_serie2))
        }

        return betplayRepository.reprintBetplay(request)
            ?.flatMap {
                val response = ResponseBetplayRechargeModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionHour
                    transactionResponseId = it.transactionId
                    user = it.user
                    serie1 = it.serie1
                    serie2 = it.serie2
                    milisegRta = it.milisegRta
                    tramaRta = it.tramaRta
                    this.documentNumber = it.documentNumber
                    balance = it.balance
                    securityCode = it.securityCode
                    currentSerie2 = it.currentSerie2
                    isRePrint = it.isRePrint
                    synNromov = it.synNromov
                    status = it.status
                    this.value = it.value
                    message = it.message
                }
                Observable.just(response)
            }

    }

    fun quickBetBetplay(document: String): Observable<ResponsePinQuickBetModel>? {
        val request = RequestPinQuickBetDTO().apply {
            reprint = false
            documentNumber = document
            userType = getPreference(R_string(R.string.shared_key_user_type))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            productCode = getPreference(R_string(R.string.code_product_betplay))
            macAddress = getPreference(R_string(R.string.shared_key_terminal_code))
            teamVol = getPreference(R_string(R.string.shared_key_terminal_code))
        }

        return betplayRepository.quickBet(request)
            ?.flatMap {
                val response = ResponsePinQuickBetModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionHour
                    status = it.status
                    message = it.message
                    millisecondsResponse = it.millisecondsResponse
                    plotResponse = it.plotResponse
                }
                Observable.just(response)
            }

    }

    fun collectBetplay(document: String, value: String): Observable<ResponsePinQuickBetModel>? {
        val request = RequestGeneratePinDTO().apply {
            user = getPreference(R_string(R.string.shared_key_seller_code))
            userType = getPreference(R_string(R.string.shared_key_user_type))
            macAddress = getPreference(R_string(R.string.shared_key_terminal_code))
            numberDocument = document
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            productCode = getPreference(R_string(R.string.code_product_betplay))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            this.value = value
            serie1 = getPreference(R_string(R.string.shared_key_current_serie1))
            serie2 = getPreference(R_string(R.string.shared_key_current_serie2))
        }
        return betplayRepository.collectBetplay(request)
            ?.flatMap {
                val response = ResponsePinQuickBetModel().apply {
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionHour
                    message = it.message
                    millisecondsResponse = it.millisecondsResponse
                    plotResponse = it.plotResponse
                    securityCode = it.securityCode
                    serie1 = it.serie1
                    serie2 = it.serie2
                    currentSerie2 = it.currentSerie2
                }
                Observable.just(response)
            }
    }

    fun checkOutBetplay(document:String,value: Int, pin: Int): Observable<ResponseBetPlayCheckOutDTO>? {

        val requestBetPlayCheckOut = RequestBetPlayCheckOut().apply {
            macAddress = getPreference(R_string(R.string.shared_key_terminal_code))
            teamVol = getPreference(R_string(R.string.shared_key_terminal_code))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            productCode = getPreference(R_string(R.string.code_product_betplay))
            channelId = getPreference(R_string(R.string.shared_key_canal_id))
            rePrint = false
            userType = getPreference(R_string(R.string.shared_key_user_type))
            serie1 = getPreference(R_string(R.string.shared_key_current_serie1))
            serie2 = getPreference(R_string(R.string.shared_key_current_serie2))
            documentNumber = document
            this.password = pin
            this.value = value
        }

        return betplayRepository.checkOutBetplay(requestBetPlayCheckOut)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }
}