package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.dto.ChanceDTO
import co.com.pagatodo.core.data.dto.LotteryDTO
import co.com.pagatodo.core.data.dto.response.ResponseChanceDTO
import co.com.pagatodo.core.data.model.ChanceModel
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.print.PrinterStatus
import io.reactivex.Observable

/**
 * Interfaz perteneciente al repositorio de chance, en ella se definen los métodos que se van a usar en el repositorio
 */
interface IChanceRepository {
    fun payChance(chanceNumbers: List<ChanceDTO>, lotteries: List<LotteryDTO>, value: Double, suggestedValue: Double,
                  isRetry: Boolean = true, transactionType: String? = null, transactionTime: Long?, sequenceTransaction: Int?): Observable<ResponseChanceDTO>?
    fun print(totalValueBetting: Double, totalValuePaid: Double, totalValueOverloaded: Double, totalValueIva: Double, raffleDate: String,raffleHour: String, lotteries: List<LotteryModel>, chances: List<ChanceModel>,
              uniqueSerial: String, digitChecking: String, stubs: String,maxRows: Int, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
}