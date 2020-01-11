package co.com.pagatodo.core.data.interactors

import android.util.Log
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.ChanceDTO
import co.com.pagatodo.core.data.dto.LotteryDTO
import co.com.pagatodo.core.data.model.*
import co.com.pagatodo.core.data.print.PrintCallback
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.repositories.IPayMillionaireRepository
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil
import com.epson.epos2.printer.Printer
import com.pos.device.printer.PrinterCallback
import io.reactivex.Observable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Singleton

interface IPayMillionaireInteractor {
    fun payPayMillionaire(chances: List<ChanceModel>, lotteries: List<LotteryModel>, valueWithoutIva: Double,
                          isRetry: Boolean = true, transactionType: String? = null, transactionTime: Long?, sequenceTransaction: Int?): Observable<GenericResponseModel>?
    fun print(totalValueBetting: Double, totalValuePaid: Double, totalValueIva: Double, raffleDate: String, lotteries: List<LotteryModel>,
              chances: List<ChanceModel>, uniqueSerial: String, digitChecking: String,
              productModel: ProductModel?, selectedModeValue: ModeValueModel?, stubs: String, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
}

@Singleton
class PayMillionaireInteractor(private var payMillionaireRepository: IPayMillionaireRepository) : IPayMillionaireInteractor {

    override fun payPayMillionaire(
        chances: List<ChanceModel>,
        lotteries: List<LotteryModel>,
        valueWithoutIva: Double,
        isRetry: Boolean,
        transactionType: String?,
        transactionTime: Long?,
        sequenceTransaction: Int?
    ): Observable<GenericResponseModel>? {

        val chanceDtos = arrayListOf<ChanceDTO>()
        chances.forEach { model ->
            chanceDtos.add(
                ChanceDTO().apply { number = model.number }
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
        return payMillionaireRepository
            .payPayMillionaire(chanceDtos, lotteriesDTO, valueWithoutIva, isRetry, transactionType, transactionTime, sequenceTransaction)
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
                    date = it.transactionDate
                    hour = it.transactionHour
                }
                Observable.just(model)
            }
    }

    override fun print(
        totalValueBetting: Double, totalValuePaid: Double,
        totalValueIva: Double, raffleDate: String,
        lotteries: List<LotteryModel>,
        chances: List<ChanceModel>,
        uniqueSerial: String,
        digitChecking: String,
        productModel: ProductModel?,
        selectedModeValue: ModeValueModel?,
        stubs: String,
        isRetry: Boolean,
        callback: (printerStatus: PrinterStatus) -> Unit?
    ) {

        val prizePlan = arrayListOf<String>()
        productModel?.parameters?.filter { it.key == R_string(R.string.sp_pm_prize_plan) }?.first()?.let {
            it.value?.split(';')?.forEach {
                prizePlan.add(it.trim())
            }
        }
        payMillionaireRepository.print(totalValueBetting, totalValuePaid, totalValueIva, raffleDate, lotteries, chances, uniqueSerial, digitChecking, prizePlan, selectedModeValue, stubs, isRetry, callback)
    }
}