package co.com.pagatodo.core.data.repositories

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
import co.com.pagatodo.core.data.model.print.RafflePrintModel
import co.com.pagatodo.core.data.print.PrinterStatus
import io.reactivex.Observable

interface IRaffleRepository {
    fun getRandomNumberRaffle(requestRaffleRandomNumberDTO: RequestRaffleRandomNumberDTO): Observable<ResponseRaffleRandomNumberDTO>?
    fun getRaffleNumberAvailable(requestRaffleAvailableDTO: RequestRaffleAvailableDTO): Observable<ResponseRaffleAvailableDTO>?
    fun registerRaffle(requestRaffleSendDTO: RequestRaffleSendDTO, isRetry: Boolean = true, transactionType: String? = null): Observable<ResponseRaffleSendDTO>?
    fun getRafflesFromLocalRoom(): Observable<List<RaffleEntityRoom>>?
    fun print(rafflePrintModel: RafflePrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
    fun saveRaffleInLocal(raffles: List<RaffleEntityRoom>): Observable<DBHelperResponse>
    fun removeAllRafflesInLocal(): Observable<DBHelperResponse>
    fun raffleAvailableRange(requestRaffleAvailableRangeDTO: RequestRaffleAvailableRangeDTO): Observable<ResponseRaffleAvailableRangeDTO>?
}