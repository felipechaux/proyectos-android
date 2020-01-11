package co.com.pagatodo.core.data.interactors

import android.text.TextUtils.replace
import co.com.pagatodo.core.data.dto.ChanceDTO
import co.com.pagatodo.core.data.dto.LotteryDTO
import co.com.pagatodo.core.data.model.ChanceModel
import co.com.pagatodo.core.data.model.GenericResponseModel
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.repositories.IChanceRepository
import co.com.pagatodo.core.util.getIntOrNull
import io.reactivex.Observable
import javax.inject.Singleton

/**
 * Interfaz utilizada para declarar los métodos que se usarán en el interactor de chance
 */
interface IChanceInteractor {
    fun payChance(chances: List<ChanceModel>, lotteries: List<LotteryModel>, valueWithoutIva: Double, suggestedValue: Double,
                  isRetry: Boolean = true, transactionType: String? = null, transactionTime: Long?, sequenceTransaction: Int?): Observable<GenericResponseModel>?
    fun print(totalValueBetting: Double, totalValuePaid: Double, totalValueOverloaded: Double,
              totalValueIva: Double, raffleDate: String,raffleHour: String, lotteries: List<LotteryModel>,
              chances: List<ChanceModel>, uniqueSerial: String, digitChecking: String, stubs: String,maxRows: Int, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
}

/**
 * Clase usada como puente con el repositorio, esta clase es la encargada crear los request DTO usados en el repositorio
 * también es la encargada de convertir los DTOs enviados por la respuesta del servicio en un modelo que es enviado
 * al viewmodel para mostrar en pantalla el resultado.
 */
@Singleton
class ChanceInteractor(private var chanceRepository: IChanceRepository) : IChanceInteractor {

    /**
     * Método usado para realizar la transacción de pago de chance
     */
    override fun payChance(chances: List<ChanceModel>, lotteries: List<LotteryModel>, valueWithoutIva: Double, suggestedValue: Double,
                           isRetry: Boolean, transactionType: String?, transactionTime: Long?, sequenceTransaction: Int?): Observable<GenericResponseModel>? {
        // Se crea el DTO con las apuestas que se van a enviar
        val chanceDtos = arrayListOf<ChanceDTO>()
        chances.forEach { model ->
            chanceDtos.add(
                ChanceDTO().apply {
                    number = model.number
                    val quantityDigits = model.number.toString().length
                    when(quantityDigits) {
                        1 -> {
                            nail = getIntOrNull(model.nail)
                        }
                        2 -> {
                            nail = getIntOrNull(model.nail)
                            paw = getIntOrNull(model.paw)
                        }
                        3 -> {
                            nail = getIntOrNull(model.nail)
                            paw = getIntOrNull(model.paw)
                            direct = getIntOrNull(model.direct)
                            combined = getIntOrNull(model.combined)
                        }
                        4 -> {
                            nail = null
                            paw = null
                            superDirect = getIntOrNull(model.direct)
                            superCombined = getIntOrNull(model.combined)
                        }
                    }
                }
            )
        }

        // Se crea el DTO con las loterías que se van a enviar
        val lotteriesDTO = arrayListOf<LotteryDTO>()
        lotteries.forEach { model ->
            lotteriesDTO.add(
                LotteryDTO().apply {
                    code = model.code
                }
            )
        }

        // Se hace el llamado al repositorio de chance para realizar la transacción
        // de este llamado se obtiene una respuesta en DTO que se mapea y se envía al viewmodel como un modelo
        return chanceRepository
            .payChance(chanceDtos, lotteriesDTO, valueWithoutIva, suggestedValue, isRetry, transactionType, transactionTime, sequenceTransaction)
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
                    hour = it.transactionHour
                    date = it.transactionDate
                }
                // Se retorna un observable con el modelo de respuesta
                Observable.just(model)
            }
    }

    /**
     * Método utilizado para realizar la impresión del tiquete de chance
     */
    override fun print(totalValueBetting: Double, totalValuePaid: Double, totalValueOverloaded: Double, totalValueIva: Double,
                       raffleDate: String,raffleHour: String, lotteries: List<LotteryModel>, chances: List<ChanceModel>,
                       uniqueSerial: String, digitChecking: String, stubs: String,maxRows: Int, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        chanceRepository.print(totalValueBetting, totalValuePaid, totalValueOverloaded, totalValueIva, raffleDate,raffleHour, lotteries, chances, uniqueSerial, digitChecking, stubs,maxRows, isRetry, callback)
    }
}