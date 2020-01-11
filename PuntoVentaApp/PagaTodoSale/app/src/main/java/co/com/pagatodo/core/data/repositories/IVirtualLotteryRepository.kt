package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.data.database.entities.ProductLotteriesEntityRoom
import co.com.pagatodo.core.data.dto.request.RequestCheckNumberLotteryDTO
import co.com.pagatodo.core.data.dto.request.RequestLotteryResultOperationDTO
import co.com.pagatodo.core.data.dto.request.RequestRandomVirtualLotteryDTO
import co.com.pagatodo.core.data.dto.response.ResponseCheckNumberLotteryDTO
import co.com.pagatodo.core.data.dto.response.ResponsePayPhysicalLotteryDTO
import co.com.pagatodo.core.data.model.VirtualLotteryModel
import co.com.pagatodo.core.data.model.print.LotteriesPrintModel
import co.com.pagatodo.core.data.model.response.LotteryNumberModel
import co.com.pagatodo.core.data.model.response.ResponseCheckNumberLotteryModel
import co.com.pagatodo.core.data.print.PrinterStatus
import io.reactivex.Observable

interface IVirtualLotteryRepository {
    fun checkPhysicalLotteryNumber(requestCheckNumberLotteryDTO: RequestCheckNumberLotteryDTO): Observable<ResponseCheckNumberLotteryDTO>?
    fun checkVirtualLotteryNumber(requestCheckNumberLotteryDTO: RequestCheckNumberLotteryDTO): Observable<ResponseCheckNumberLotteryDTO>?
    fun getLotteriesRoom(productCode: String): Observable<List<ProductLotteriesEntityRoom>>?
    fun payPhysicalLottery(requestLotteryResultOperationDTO: RequestLotteryResultOperationDTO, isRetry: Boolean = true, transactionType: String? = null): Observable<ResponsePayPhysicalLotteryDTO>?
    fun payVirtualLottery(requestLotteryResultOperationDTO: RequestLotteryResultOperationDTO, isRetry: Boolean = true, transactionType: String? = null): Observable<ResponsePayPhysicalLotteryDTO>?
    fun print(lotteriesPrintModel: LotteriesPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
    fun printFractionsAvailables(lotteriesPrintModel: ResponseCheckNumberLotteryModel, model: VirtualLotteryModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?)
    fun generateRandomVirtualLottery(requestRandomVirtualLotteryDTO: RequestRandomVirtualLotteryDTO): Observable<ResponseCheckNumberLotteryDTO>?
}