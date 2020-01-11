package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.database.entities.PromotionalEntityRoom
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
import co.com.pagatodo.core.di.repository.DaggerMaxiChanceRepositoryComponent
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
class MaxiChanceRepository: BaseRepository(),
    IMaxiChanceRepository {

    @Inject
    lateinit var chanceProductsPrint: IChanceProductsPrint

    init {
        DaggerMaxiChanceRepositoryComponent
            .builder()
            .printChanceProductModule(PrintChanceProductModule())
            .build().inject(this)
    }

    override fun getPromotionalForSuperChanceRoom(): Observable<PromotionalEntityRoom>?{
        val name = R_string(R.string.product_name_maxichance)
        return PagaTodoApplication.getDatabaseInstance()
            ?.promotionalDao()
            ?.getPromotionalByName(name)
            ?.flatMap {
                val prom = it.promotional
                val response = PromotionalEntityRoom().apply {
                this.id = prom?.id ?: 0
                this.bettingAmount = prom?.bettingAmount
                this.quantityFigures = prom?.quantityFigures
                this.lotteriesQuantity = prom?.lotteriesQuantity
                this.isGenerateRandom = prom?.isGenerateRandom ?: false
                this.name = prom?.name
                this.openValue = prom?.openValue ?: false
                this.modalitiesValues = it.modalitiesValues
            }
            Observable.just(response)
        }
    }

    override fun payMaxichance(chanceNumbers: List<ChanceDTO>, lotteries: List<LotteryDTO>, value: Double, isRetry: Boolean,
                               transactionType: String?, transactionTime: Long?, sequenceTransaction: Int?): Observable<ResponseChanceDTO>? {
        val requestChanceDTO = RequestChanceDTO().apply {
            serie1 = getPreference<String>(R_string(R.string.shared_key_current_serie1)).toUpperCase()
            serie2 = getPreference(R_string(R.string.shared_key_current_serie2))
            userType = getPreference(R_string(R.string.shared_key_user_type))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            canalId = getPreference(R_string(R.string.shared_key_canal_id))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            productCode = getPreference(R_string(R.string.code_product_maxichance))
            promotionId = 1
            this.chanceNumbers = chanceNumbers
            this.lotteries = lotteries
            this.value = value
            this.suggestedValue = value
            this.transactionType = transactionType
            this.transactionTime = transactionTime
            this.sequenceTransaction = sequenceTransaction
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
        chanceProductsPrint.printMaxiChance(printModel, games, isRetry, callback)

    }
}