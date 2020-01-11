package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.data.database.DBHelperOperations
import co.com.pagatodo.core.data.database.DBHelperResponse
import co.com.pagatodo.core.data.database.entities.RaffleEntityRoom
import co.com.pagatodo.core.data.dto.request.RequestRaffleAvailableDTO
import co.com.pagatodo.core.data.dto.request.RequestRaffleAvailableRangeDTO
import co.com.pagatodo.core.data.dto.request.RequestRaffleRandomNumberDTO
import co.com.pagatodo.core.data.dto.request.RequestRaffleSendDTO
import co.com.pagatodo.core.data.dto.response.ResponseRaffleAvailableDTO
import co.com.pagatodo.core.data.dto.response.ResponseRaffleAvailableRangeDTO
import co.com.pagatodo.core.data.dto.response.ResponseRaffleRandomNumberDTO
import co.com.pagatodo.core.data.dto.response.ResponseRaffleSendDTO
import co.com.pagatodo.core.data.model.TransactionType
import co.com.pagatodo.core.data.model.print.RafflePrintModel
import co.com.pagatodo.core.data.print.IRafflePrint
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.network.ApiFactory
import co.com.pagatodo.core.di.print.PrintRaffleModule
import co.com.pagatodo.core.di.repository.DaggerRaffleRepositoryComponent
import co.com.pagatodo.core.util.StorageUtil
import io.reactivex.Observable
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RaffleRepository: BaseRepository(),
    IRaffleRepository {

    @Inject
    lateinit var rafflePrint: IRafflePrint

    init {
        DaggerRaffleRepositoryComponent.builder()
            .printRaffleModule(PrintRaffleModule())
            .build().inject(this)
    }

    override fun getRaffleNumberAvailable(requestRaffleAvailableDTO: RequestRaffleAvailableDTO): Observable<ResponseRaffleAvailableDTO>? {
        return ApiFactory.build()?.getRaffleAvailable(requestRaffleAvailableDTO)
    }

    override fun registerRaffle(requestDTO: RequestRaffleSendDTO, isRetry: Boolean,
                                transactionType: String?): Observable<ResponseRaffleSendDTO>? {
        transactionType?.let {
            requestDTO.transactionType = it
        }

        if (!isRetry) {
            return ApiFactory.build()?.registerRaffle(requestDTO)
        }

        return ApiFactory.build()?.registerRaffle(requestDTO)
            ?.onErrorResumeNext { throwable: Throwable ->
                if (throwable is SocketTimeoutException) {
                    requestDTO.transactionType = TransactionType.RETRY.rawValue
                    ApiFactory
                        .build(StorageUtil.getTimeoutRetry())
                        ?.registerRaffle(requestDTO)
                        ?.retry(StorageUtil.getRetryCount())
                } else {
                    Observable.error(throwable)
                }
            }

    }

    override fun getRandomNumberRaffle(requestRaffleRandomNumberDTO: RequestRaffleRandomNumberDTO): Observable<ResponseRaffleRandomNumberDTO>? {
        return ApiFactory.build()?.getRandomNumberRaffle(requestRaffleRandomNumberDTO)
    }

    override fun getRafflesFromLocalRoom(): Observable<List<RaffleEntityRoom>>? {
        return PagaTodoApplication.getDatabaseInstance()?.raffleDao()?.getAllAsync()
    }

    override fun saveRaffleInLocal(raffles: List<RaffleEntityRoom>): Observable<DBHelperResponse> {
        val response = DBHelperResponse().apply {
            status = true
            operation = DBHelperOperations.insertRows
        }

        val dbInstance = PagaTodoApplication.getDatabaseInstance()
        try {
            dbInstance?.raffleDao()?.insertAll(raffles)
        }
        catch (e: Exception) {
            response.status = false
        }
        return Observable.just(response)
    }

    override fun removeAllRafflesInLocal(): Observable<DBHelperResponse> {
        val response = DBHelperResponse().apply {
            status = true
            operation = DBHelperOperations.deleteAllTable
        }

        try {
            PagaTodoApplication.getDatabaseInstance()?.raffleDao()?.deleteAll()
        }
        catch (e: Exception) {
            response.status = false
        }
        return Observable.just(response)
    }

    override fun print(rafflePrintModel: RafflePrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {

        rafflePrint.print(rafflePrintModel, isRetry, callback)
    }

    override fun raffleAvailableRange(requestRaffleAvailableRangeDTO: RequestRaffleAvailableRangeDTO): Observable<ResponseRaffleAvailableRangeDTO>? {
        return ApiFactory.build()?.raffleAvailableRange(requestRaffleAvailableRangeDTO)
    }
}