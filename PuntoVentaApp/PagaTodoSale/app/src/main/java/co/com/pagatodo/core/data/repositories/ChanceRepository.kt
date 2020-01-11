package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.ChanceDTO
import co.com.pagatodo.core.data.dto.LotteryDTO
import co.com.pagatodo.core.data.dto.request.RequestChanceDTO
import co.com.pagatodo.core.data.dto.response.ResponseChanceDTO
import co.com.pagatodo.core.data.model.ChanceModel
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.TransactionType
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.print.chance.IChanceProductsPrint
import co.com.pagatodo.core.di.print.PrintChanceProductModule
import co.com.pagatodo.core.di.repository.DaggerChanceRepositoryComponent
import co.com.pagatodo.core.network.ApiFactory
import co.com.pagatodo.core.util.CurrencyUtils
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.StorageUtil
import io.reactivex.Observable
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Clase repositorio que se encarga de realizar el llamado de los servicios del producto chance y el manejo de los datos del producto
 * Esta clase hereda de la interfaz IChanceRepository
 */
@Singleton
class ChanceRepository: BaseRepository(), IChanceRepository {

    // Inyección de la variable ChanceProductsPrint para realizar la impresión de los productos de chance
    @Inject
    lateinit var chanceProductsPrint: IChanceProductsPrint
    lateinit var chanceProductsPrintQ2: IChanceProductsPrint

    /**
     * Inicialización de la clase repositorio
     */
    init {
        // Inicialización del componente de dagger para realizar la inyección de dependencias
        DaggerChanceRepositoryComponent
            .builder()
            .printChanceProductModule(PrintChanceProductModule())
            .build().inject(this)
    }

    /**
     * Método utilizado para realizar el pago del producto chance
     */
    override fun payChance(chanceNumbers: List<ChanceDTO>, lotteries: List<LotteryDTO>, value: Double, suggestedValue: Double,
                           isRetry: Boolean, transactionType: String?, transactionTime: Long?, sequenceTransaction: Int?): Observable<ResponseChanceDTO>? {
        // Se crea el request DTO que se va a enviar al servicio
        val requestChanceDTO = RequestChanceDTO().apply {
            serie1 = getPreference(R_string(R.string.shared_key_current_serie1))
            serie2 = getPreference(R_string(R.string.shared_key_current_serie2))
            userType = getPreference(R_string(R.string.shared_key_user_type))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            canalId = getPreference(R_string(R.string.shared_key_canal_id))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            productCode = R_string(R.string.code_chance)
            promotionId = 0
            this.chanceNumbers = chanceNumbers
            this.lotteries = lotteries
            this.value = value
            this.suggestedValue = suggestedValue
            this.transactionTime = transactionTime
            this.sequenceTransaction = sequenceTransaction
            this.transactionType = transactionType
        }

        // Se valida si no es un reintento, en ese caso se hace el llamado simple del servicio
        if (!isRetry) {
            return ApiFactory.build()?.payChance(requestChanceDTO)
        }

        // LLamado al servicio de pagar chance con la configuración del ApiFactory
        return ApiFactory.build()?.payChance(requestChanceDTO)
            ?.onErrorResumeNext { throwable: Throwable ->
                // En caso de que exista algún error al momento de realizar la transacción este realizará 3 intentos más
                if (throwable is SocketTimeoutException) {
                    // Llamado del servicio esta vez como un reintento
                    requestChanceDTO.transactionType = TransactionType.RETRY.rawValue
                    ApiFactory
                        .build(StorageUtil.getTimeoutRetry())
                        ?.payChance(requestChanceDTO)
                        ?.retry(StorageUtil.getRetryCount())
                } else {
                    // En caso de que el error no sea por un timeout se retorna el error obtenido
                    Observable.error(throwable)
                }
            }
    }

    /**
     * Método utilizado para realizar la impresión del tiquete
     */
    override fun print(totalValueBetting: Double, totalValuePaid: Double, totalValueOverloaded: Double, totalValueIva: Double, raffleDate: String,raffleHour: String, lotteries: List<LotteryModel>,
                       chances: List<ChanceModel>, uniqueSerial: String, digitChecking: String, stubs: String,maxRows: Int, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        val iva = (CurrencyUtils.getIvaPercentage() * 100).toInt()
        chanceProductsPrint.printChance(totalValueBetting, totalValuePaid, totalValueOverloaded, totalValueIva, raffleDate,raffleHour, lotteries, chances, iva, getSellerCode(), uniqueSerial, digitChecking, stubs,maxRows, isRetry, callback)


    }
}