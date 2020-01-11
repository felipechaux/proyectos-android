package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.database.entities.ProductParameterEntityRoom
import co.com.pagatodo.core.data.dto.ChanceDTO
import co.com.pagatodo.core.data.dto.LotteryDTO
import co.com.pagatodo.core.data.dto.request.RequestChanceDTO
import co.com.pagatodo.core.data.dto.response.ResponseChanceDTO
import co.com.pagatodo.core.data.model.SuperchanceModel
import co.com.pagatodo.core.data.model.TransactionType
import co.com.pagatodo.core.data.model.print.BasePrintModel
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.print.chance.IChanceProductsPrint
import co.com.pagatodo.core.di.print.PrintChanceProductModule
import co.com.pagatodo.core.di.repository.DaggerSuperChanceRepositoryComponent
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
class SuperChanceRepository: BaseRepository(),
    ISuperChanceRepository {

    @Inject
    lateinit var chanceProductsPrint: IChanceProductsPrint

    init {
        DaggerSuperChanceRepositoryComponent
            .builder()
            .printChanceProductModule(PrintChanceProductModule())
            .build().inject(this)
    }

    override fun getProductParametersForSuperChanceRoom(): Observable<List<ProductParameterEntityRoom>>? {
        val data = mutableListOf<ProductParameterEntityRoom>()
        val productCode = getPreference<String>(R_string(R.string.code_product_superchance))
        PagaTodoApplication.getDatabaseInstance()?.productDao()?.getAllByCode(productCode)?.let {
            it.productParameters?.forEach {
                data.add(it)
            }
        }

        return Observable.create {
            it.onNext(data)
            it.onComplete()
        }
    }

    override fun paySuperchance(chanceNumbers: List<ChanceDTO>, lotteries: List<LotteryDTO>, value: Double, isRetry: Boolean,
                                transactionType: String?, transactionTime: Long?, sequenceTransaction: Int?): Observable<ResponseChanceDTO>? {
        val requestChanceDTO = RequestChanceDTO().apply {
            serie1 = getPreference(R_string(R.string.shared_key_current_serie1))
            serie2 = getPreference(R_string(R.string.shared_key_current_serie2))
            userType = getPreference(R_string(R.string.shared_key_user_type))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            canalId = getPreference(R_string(R.string.shared_key_canal_id) )
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            productCode = getPreference(R_string(R.string.code_product_superchance))
            promotionId = 0
            verificationCode = CurrencyUtils.generateRandomAlphanumericString(12)
            this.chanceNumbers = chanceNumbers
            this.lotteries = lotteries
            this.value = value
            this.suggestedValue = value
            this.transactionType = transactionType
            this.transactionTime = transactionTime
            this.sequenceTransaction = sequenceTransaction
        }

        if (!isRetry) {
            return ApiFactory.build()?.payChance(requestChanceDTO)
        }

        return ApiFactory.build()?.payChance(requestChanceDTO)
            ?.onErrorResumeNext { throwable: Throwable ->
                if (throwable is SocketTimeoutException) {
                    requestChanceDTO.transactionType = TransactionType.RETRY.rawValue
                    ApiFactory
                        .build(StorageUtil.getTimeoutRetry())
                        ?.payChance(requestChanceDTO)
                        ?.retry(StorageUtil.getRetryCount())
                } else {
                    Observable.error(throwable)
                }
            }
    }

    override fun print(printModel: BasePrintModel, games: List<SuperchanceModel>, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        printModel.iva = (CurrencyUtils.getIvaPercentage() * 100).toInt()
        printModel.consultant = getSellerCode()
        chanceProductsPrint.printSuperChance(printModel, games, isRetry, callback)
    }
}