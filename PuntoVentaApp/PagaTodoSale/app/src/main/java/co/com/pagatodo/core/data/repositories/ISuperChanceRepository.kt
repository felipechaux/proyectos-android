package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.database.entities.*
import co.com.pagatodo.core.data.dto.ChanceDTO
import co.com.pagatodo.core.data.dto.LotteryDTO
import co.com.pagatodo.core.data.dto.response.ResponseChanceDTO
import co.com.pagatodo.core.data.model.SuperchanceModel
import co.com.pagatodo.core.data.model.print.BasePrintModel
import co.com.pagatodo.core.data.print.PrinterStatus
import io.reactivex.Observable

interface ISuperChanceRepository {
    fun getProductParametersForSuperChanceRoom(): Observable<List<ProductParameterEntityRoom>>?
    fun paySuperchance(chanceNumbers: List<ChanceDTO>, lotteries: List<LotteryDTO>, value: Double, isRetry: Boolean = true,
                       transactionType: String? = null, transactionTime: Long?, sequenceTransaction: Int?): Observable<ResponseChanceDTO>?
    fun print(printModel: BasePrintModel, games: List<SuperchanceModel>, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
}