package co.com.pagatodo.core.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.database.entities.ProductLotteriesEntityRoom
import co.com.pagatodo.core.data.dto.request.RequestCheckNumberLotteryDTO
import co.com.pagatodo.core.data.dto.request.RequestLotteryResultOperationDTO
import co.com.pagatodo.core.data.dto.request.RequestRandomVirtualLotteryDTO
import co.com.pagatodo.core.data.dto.response.ResponseCheckNumberLotteryDTO
import co.com.pagatodo.core.data.dto.response.ResponsePayPhysicalLotteryDTO
import co.com.pagatodo.core.data.model.TransactionType
import co.com.pagatodo.core.data.model.VirtualLotteryModel
import co.com.pagatodo.core.data.model.print.LotteriesPrintModel
import co.com.pagatodo.core.data.model.response.LotteryNumberModel
import co.com.pagatodo.core.data.model.response.ResponseCheckNumberLotteryModel
import co.com.pagatodo.core.data.print.ILotteriesPrint
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.print.PrintLotteryModule
import co.com.pagatodo.core.di.repository.DaggerVirtualLotteryRepositoryComponent
import co.com.pagatodo.core.network.ApiFactory
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.StorageUtil
import io.reactivex.Observable
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VirtualLotteryRepository: BaseRepository(),
    IVirtualLotteryRepository {

    @Inject
    lateinit var lotteriesPrint: ILotteriesPrint

    init {
        DaggerVirtualLotteryRepositoryComponent
            .builder()
            .printLotteryModule(PrintLotteryModule())
            .build().inject(this)
    }

    override fun checkPhysicalLotteryNumber(requestCheckNumberLotteryDTO: RequestCheckNumberLotteryDTO): Observable<ResponseCheckNumberLotteryDTO>? {
        return ApiFactory.build()?.checkPhysicalLotteryNumber(requestCheckNumberLotteryDTO)
    }

    override fun checkVirtualLotteryNumber(requestCheckNumberLotteryDTO: RequestCheckNumberLotteryDTO): Observable<ResponseCheckNumberLotteryDTO>? {
        return ApiFactory.build()?.checkVirtualLotteryNumber(requestCheckNumberLotteryDTO)
    }

    override fun getLotteriesRoom(productCode: String): Observable<List<ProductLotteriesEntityRoom>>? {
        val a = PagaTodoApplication.getDatabaseInstance()?.productLotteriesDao()?.getAll()
        val data = PagaTodoApplication.getDatabaseInstance()?.productLotteriesDao()?.getAllByProductCode(productCode)
        return Observable.just(data)
    }

    override fun payPhysicalLottery(requestDTO: RequestLotteryResultOperationDTO, isRetry: Boolean, transactionType: String?): Observable<ResponsePayPhysicalLotteryDTO>? {
        transactionType?.let {
            requestDTO.transactionType = transactionType
        }

        if (!isRetry) {
            return ApiFactory.build()?.payPhysicalLottery(requestDTO)
        }

        return ApiFactory.build()?.payPhysicalLottery(requestDTO)
            ?.onErrorResumeNext { throwable: Throwable ->
                if (throwable is SocketTimeoutException) {
                    requestDTO.transactionType = TransactionType.RETRY.rawValue
                    ApiFactory
                        .build(StorageUtil.getTimeoutRetry())
                        ?.payPhysicalLottery(requestDTO)
                        ?.retry(StorageUtil.getRetryCount())
                } else {
                    Observable.error(throwable)
                }
            }
    }

    override fun payVirtualLottery(requestDTO: RequestLotteryResultOperationDTO, isRetry: Boolean, transactionType: String?): Observable<ResponsePayPhysicalLotteryDTO>? {
        transactionType?.let {
            requestDTO.transactionType = transactionType
        }

        if (!isRetry) {
            return ApiFactory.build()?.payVirtualLottery(requestDTO)
        }

        return ApiFactory.build()?.payVirtualLottery(requestDTO)
            ?.onErrorResumeNext { throwable: Throwable ->
                if (throwable is SocketTimeoutException) {
                    requestDTO.transactionType = TransactionType.RETRY.rawValue
                    ApiFactory
                        .build(StorageUtil.getTimeoutRetry())
                        ?.payVirtualLottery(requestDTO)
                        ?.retry(StorageUtil.getRetryCount())
                } else {
                    Observable.error(throwable)
                }
            }
    }

    override fun generateRandomVirtualLottery(requestRandomVirtualLotteryDTO: RequestRandomVirtualLotteryDTO): Observable<ResponseCheckNumberLotteryDTO>? {
        return ApiFactory.build()?.generateRandomVirtualLottery(requestRandomVirtualLotteryDTO)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun print(lotteriesPrintModel: LotteriesPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        lotteriesPrint.print(lotteriesPrintModel, isRetry, callback)

    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun printFractionsAvailables(lotteriesPrintModel: ResponseCheckNumberLotteryModel, model: VirtualLotteryModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?
    ) {
        lotteriesPrint.printFractionsAvailable(lotteriesPrintModel, model, isRetry, callback)
    }

}