package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.LotteryDTO
import co.com.pagatodo.core.data.dto.SuperAstroDTO
import co.com.pagatodo.core.data.dto.request.RequestSuperAstroDTO
import co.com.pagatodo.core.data.model.*
import co.com.pagatodo.core.data.model.print.SuperAstroPrintModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.repositories.ISuperAstroRepository
import co.com.pagatodo.core.di.DaggerSuperAstroComponent
import co.com.pagatodo.core.di.SuperAstroModule
import co.com.pagatodo.core.util.DateUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.StorageUtil
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SuperAstroInteractor {

    @Inject
    lateinit var superAstroRepository: ISuperAstroRepository

    init {
        DaggerSuperAstroComponent.builder().superAstroModule(SuperAstroModule()).build().inject(this)
    }

    fun getProductParametersForSuperAstro(): Observable<List<KeyValueModel>>? {
        return superAstroRepository
            .getProductParametersForSuperAstroRoom()
            ?.flatMap {
                val response = arrayListOf<KeyValueModel>()
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

    fun paySuperAstro(superastroList: List<SuperAstroModel>, lotteries: List<LotteryModel>, value: Double, isRetry: Boolean = true,
                      transactionType: String? = null, transactionTime: Long?, sequenceTransaction: Int?): Observable<GenericResponseModel>? {
        val superastroDtos = arrayListOf<SuperAstroDTO>()
        superastroList.forEach { model ->
            superastroDtos.add(
                SuperAstroDTO().apply {
                    number = model.number
                    sign = model.zodiacSelected?.split("-")?.get(0)
                    this.value = model.value
                }
            )
        }

        val lotteriesDTO = arrayListOf<LotteryDTO>()
        lotteries.forEach { model ->
            lotteriesDTO.add(
                LotteryDTO().apply {
                    code = model.code
                }
            )
        }

        val requestChanceDTO = createRequestChanceDTO(superastroDtos, lotteriesDTO, value, transactionTime, sequenceTransaction)
        return superAstroRepository
            .paySuperAstro(requestChanceDTO, isRetry, transactionType)
            ?.flatMap {
                val model = GenericResponseModel().apply {
                    isSuccess = it.isSuccess?:false
                    message = it.message
                    serie1 = it.serie1
                    currentSerie2 = it.currentSerie2
                    answerBets = it.answerBets
                    totalValueBetting = it.totalValueBetting?.toDouble()
                    totalValueIva = it.totalValueIva?.toDouble()
                    totalValueOverloaded = it.totalValueOverloaded?.toDouble()
                    totalValuePaid = it.totalValuePaid?.toDouble()
                    digitChecking = it.digitChecking
                    checkNumber = it.checkNumber
                }
                Observable.just(model)
            }
    }

    private fun createRequestChanceDTO(numberList: List<SuperAstroDTO>, lotteries: List<LotteryDTO>, value: Double, transactionTime: Long?, sequenceTransaction: Int?): RequestSuperAstroDTO {
        return RequestSuperAstroDTO().apply {
            serie1 = getPreference(R_string(R.string.shared_key_current_serie1))
            serie2 = getPreference(R_string(R.string.shared_key_current_serie2))
            userType = getPreference(R_string(R.string.shared_key_user_type))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            canalId = getPreference(R_string(R.string.shared_key_canal_id))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            productCode = getPreference(R_string(R.string.code_product_superastro))
            chanceNumbers = numberList
            this.lotteries = lotteries
            this.value = value
            this.transactionTime = transactionTime
            this.sequenceTransaction = sequenceTransaction
        }
    }

    fun getModalities(): Observable<List<ModalityModel>>? {
        return superAstroRepository
            .getModalitiesRoom()
            ?.flatMap {
                val response = arrayListOf<ModalityModel>()
                it.forEach {  ent ->
                    val modalityModel = ModalityModel().apply {
                        code = ent.code
                        description = ent.description ?: ""
                    }
                    response.add(modalityModel)
                }
                Observable.just(response)
            }
    }

    fun print(printModel: SuperAstroPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        superAstroRepository.print(printModel, isRetry, callback)
    }
}