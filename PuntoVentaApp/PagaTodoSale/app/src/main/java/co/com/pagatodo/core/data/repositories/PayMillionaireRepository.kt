package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.ChanceDTO
import co.com.pagatodo.core.data.dto.LotteryDTO
import co.com.pagatodo.core.data.dto.request.RequestChanceDTO
import co.com.pagatodo.core.data.dto.response.ResponseChanceDTO
import co.com.pagatodo.core.data.model.ChanceModel
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.ModeValueModel
import co.com.pagatodo.core.data.model.TransactionType
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.print.chance.IChanceProductsPrint
import co.com.pagatodo.core.di.print.PrintChanceProductModule
import co.com.pagatodo.core.di.repository.DaggerMillionairePaymentRepositoryComponent
import co.com.pagatodo.core.network.ApiFactory
import co.com.pagatodo.core.util.CurrencyUtils
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.StorageUtil
import io.reactivex.Observable
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PayMillionaireRepository: BaseRepository(),
    IPayMillionaireRepository {

    @Inject
    lateinit var chanceProductsPrint: IChanceProductsPrint

    init {
        DaggerMillionairePaymentRepositoryComponent
            .builder()
            .printChanceProductModule(PrintChanceProductModule())
            .build().inject(this)
    }

    override fun payPayMillionaire(
        chanceNumbers: List<ChanceDTO>,
        lotteries: List<LotteryDTO>,
        value: Double,
        isRetry: Boolean,
        transactionType: String?,
        transactionTime: Long?,
        sequenceTransaction: Int?
    ): Observable<ResponseChanceDTO>? {

        val requestChanceDTO = RequestChanceDTO().apply {
            serie1 = getPreference(R_string(R.string.shared_key_current_serie1))
            serie2 = getPreference(R_string(R.string.shared_key_current_serie2))
            userType = getPreference(R_string(R.string.shared_key_user_type))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            canalId = getPreference(R_string(R.string.shared_key_canal_id))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            productCode = R_string(R.string.code_product_paymillionaire)
            this.chanceNumbers = chanceNumbers
            this.lotteries = lotteries
            this.value = value
            this.transactionTime = transactionTime
            this.sequenceTransaction = sequenceTransaction
            this.transactionType = transactionType
        }

        if (!isRetry) {
            return ApiFactory.build()?.payPaymillionaire(requestChanceDTO)
        }

        return ApiFactory.build()?.payPaymillionaire(requestChanceDTO)
            ?.onErrorResumeNext { throwable: Throwable ->
                //ConnectinException
                if (throwable is SocketTimeoutException) {
                    requestChanceDTO.transactionType = TransactionType.RETRY.rawValue
                    ApiFactory
                        .build(StorageUtil.getTimeoutRetry())
                        ?.payPaymillionaire(requestChanceDTO)
                        ?.retry(StorageUtil.getRetryCount())
                } else {
                    Observable.error(throwable)
                }
            }
    }

    override fun print(
        totalValueBetting: Double, totalValuePaid: Double, totalValueIva: Double,
        raffleDate: String, lotteries: List<LotteryModel>, chances: List<ChanceModel>,
        uniqueSerial: String, digitChecking: String, prizePlan: List<String>,
        selectedModeValue: ModeValueModel?, stubs: String, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        val iva = (CurrencyUtils.getIvaPercentage() * 100).toInt()
        chanceProductsPrint.printPaymillionaire(totalValueBetting, totalValuePaid, totalValueIva, raffleDate, lotteries, chances, iva, getSellerCode(), uniqueSerial, digitChecking, prizePlan, selectedModeValue, stubs, isRetry, callback)

    }
}