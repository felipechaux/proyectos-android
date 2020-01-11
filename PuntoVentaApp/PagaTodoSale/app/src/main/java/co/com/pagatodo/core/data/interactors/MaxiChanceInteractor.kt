package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.data.dto.ChanceDTO
import co.com.pagatodo.core.data.dto.LotteryDTO
import co.com.pagatodo.core.data.model.GenericResponseModel
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.PromotionModel
import co.com.pagatodo.core.data.model.SuperchanceModel
import co.com.pagatodo.core.data.model.print.BasePrintModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.repositories.IMaxiChanceRepository
import co.com.pagatodo.core.di.DaggerMaxiChanceComponent
import co.com.pagatodo.core.di.MaxiChanceModule
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

interface IMaxiChanceInteractor {
    fun getPromotionalForMaxiChance(): Observable<PromotionModel>?
    fun payMaxichance(superchanceList: List<SuperchanceModel>, lotteries: List<LotteryModel>, value: Double,
                      isRetry: Boolean = true, transactionType: String? = null, transactionTime: Long?, sequenceTransaction: Int?): Observable<GenericResponseModel>?
    fun print(printModel: BasePrintModel, games: List<SuperchanceModel>, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
}

@Singleton
class MaxiChanceInteractor: IMaxiChanceInteractor {

    @Inject
    lateinit var maxiChanceRepository: IMaxiChanceRepository

    init {
        DaggerMaxiChanceComponent.builder().maxiChanceModule(MaxiChanceModule()).build().inject(this)
    }

    override fun getPromotionalForMaxiChance(): Observable<PromotionModel>? {
        return maxiChanceRepository
            .getPromotionalForSuperChanceRoom()
            ?.flatMap {
                val modalityModel = PromotionModel().apply {
                    bettingAmount = it.bettingAmount
                    quantityFigures = it.quantityFigures
                    lotteriesQuantity = it.lotteriesQuantity
                    generateRandom = it.isGenerateRandom
                    id = it.id
                    name = it.name
                    openValue = it.openValue
                    modalitiesValues = it.modalitiesValues?.map { modality ->
                        modality.values
                    }
                }
                Observable.just(modalityModel)
            }
    }

    override fun payMaxichance(superchanceList: List<SuperchanceModel>, lotteries: List<LotteryModel>, value: Double,
                               isRetry: Boolean, transactionType: String?, transactionTime: Long?, sequenceTransaction: Int?): Observable<GenericResponseModel>? {
        val chanceDtos = arrayListOf<ChanceDTO>()

        superchanceList.forEach { model ->
            chanceDtos.add(
                ChanceDTO().apply {
                    number = model.number
                    val quantityDigits = model.number.length
                    val number = model.value.toInt()

                    if (quantityDigits == 3) {
                        direct = number
                    } else if(quantityDigits == 4) {
                        superDirect = number
                    }
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

        return maxiChanceRepository
            .payMaxichance(chanceDtos, lotteriesDTO, value, isRetry, transactionType, transactionTime, sequenceTransaction)
            ?.flatMap {
                val model = GenericResponseModel().apply {
                    isSuccess = it.isSuccess?:false
                    message = it.message
                    serie1 = it.serie1
                    currentSerie2 = it.currentSerie2
                    uniqueSerial = it.uniqueSerial
                    digitChecking = it.digitChecking
                    totalValueIva = it.totalValueIva?.toDouble()
                    totalValuePaid = it.totalValuePaid?.toDouble()
                    totalValueOverloaded = it.totalValueOverloaded?.toDouble()
                    totalValueBetting = it.totalValueBetting?.toDouble()
                    answerBets = it.answerBets
                    date = it.transactionDate
                    hour = it.transactionHour
                }
                Observable.just(model)
            }
    }

    override fun print(printModel: BasePrintModel, games: List<SuperchanceModel>, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        maxiChanceRepository.print(printModel, games, isRetry, callback)
    }
}