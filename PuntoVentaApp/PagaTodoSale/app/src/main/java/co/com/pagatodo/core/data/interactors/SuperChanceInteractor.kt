package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.data.dto.ChanceDTO
import co.com.pagatodo.core.data.dto.LotteryDTO
import co.com.pagatodo.core.data.model.*
import co.com.pagatodo.core.di.DaggerSuperChanceComponent
import co.com.pagatodo.core.data.model.print.BasePrintModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.repositories.ISuperChanceRepository
import co.com.pagatodo.core.di.SuperChanceModule
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SuperChanceInteractor {

    @Inject
    lateinit var superChanceRepository: ISuperChanceRepository

    init {
        DaggerSuperChanceComponent.builder().superChanceModule(SuperChanceModule()).build().inject(this)
    }

    fun getProductParametersForSuperChance(): Observable<List<KeyValueModel>>? {
        return superChanceRepository
            .getProductParametersForSuperChanceRoom()
            ?.flatMap {
                val response = mutableListOf<KeyValueModel>()
                it.forEach {ent ->
                    val model = KeyValueModel().apply {
                        key = ent.key
                        value = ent.value
                    }
                    response.add(model)
                }
                Observable.just(response)
            }
    }

    fun paySuperchance(superchanceList: List<SuperchanceModel>, lotteries: List<LotteryModel>, value: Double, isRetry: Boolean = true,
                       transactionType: String? = null, transactionTime: Long?, sequenceTransaction: Int?): Observable<GenericResponseModel>? {
        val chanceDtos = arrayListOf<ChanceDTO>()

        superchanceList.forEach { model ->
            chanceDtos.add(
                ChanceDTO().apply {
                    number = model.number
                    val quantityDigits = model.number.length

                    if (quantityDigits == 3) {
                        direct = model.value.toInt()
                    }
                    else if(quantityDigits == 4) {
                        superDirect = model.value.toInt()
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

        return superChanceRepository
            .paySuperchance(chanceDtos, lotteriesDTO, value, isRetry, transactionType, transactionTime, sequenceTransaction)
            ?.flatMap {
                val model = GenericResponseModel().apply {
                    isSuccess = it.isSuccess?:false
                    message = it.message
                    serie1 = it.serie1
                    currentSerie2 = it.currentSerie2
                    uniqueSerial = it.uniqueSerial
                    totalValueOverloaded = it.totalValueOverloaded?.toDouble()
                    totalValueBetting = it.totalValueBetting?.toDouble()
                    totalValuePaid = it.totalValuePaid?.toDouble()
                    totalValueIva = it.totalValueIva?.toDouble()
                    answerBets = it.answerBets
                    digitChecking = it.digitChecking
                    date = it.transactionDate
                    hour = it.transactionHour
                }
                Observable.just(model)
            }
    }

    fun print(printModel: BasePrintModel, games: List<SuperchanceModel>, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        superChanceRepository.print(printModel, games, isRetry, callback)
    }
}