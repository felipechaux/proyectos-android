package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.dto.ChanceDTO
import co.com.pagatodo.core.data.dto.LotteryDTO
import co.com.pagatodo.core.data.dto.response.ResponseChanceDTO
import co.com.pagatodo.core.data.model.ChanceModel
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.ModeValueModel
import co.com.pagatodo.core.data.print.PrinterStatus
import io.reactivex.Observable

interface IPayMillionaireRepository {
    fun payPayMillionaire(chanceNumbers: List<ChanceDTO>, lotteries: List<LotteryDTO>, value: Double,
                          isRetry: Boolean = true, transactionType: String? = null, transactionTime: Long?, sequenceTransaction: Int?): Observable<ResponseChanceDTO>?
    fun print(totalValueBetting: Double, totalValuePaid: Double, totalValueIva: Double, raffleDate: String,
              lotteries: List<LotteryModel>, chances: List<ChanceModel>, uniqueSerial: String, digitChecking: String,
              prizePlan: List<String>, selectedModeValue: ModeValueModel?, stubs: String, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
}