package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.database.entities.ProductParameterEntityRoom
import co.com.pagatodo.core.data.dto.request.RequestQueryPinWalletDTO
import co.com.pagatodo.core.data.dto.request.RequestVirtualWalletActivatePinDTO
import co.com.pagatodo.core.data.dto.response.ResponseActivatePinDTO
import co.com.pagatodo.core.data.dto.response.ResponseQueryPinDTO
import co.com.pagatodo.core.data.model.TransactionType
import co.com.pagatodo.core.data.model.print.VirtualWalletPrintModel
import co.com.pagatodo.core.data.print.IVirtualWalletPrint
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.print.VirtualWalletPrint
import co.com.pagatodo.core.di.print.PrintVirtualWalletModule
import co.com.pagatodo.core.di.repository.DaggerVirtualWalletRepositoryComponent
import co.com.pagatodo.core.network.ApiFactory
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.StorageUtil
import io.reactivex.Observable
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton


interface IVirtualWalletRepository {
    fun getProductParameters(): Observable<List<ProductParameterEntityRoom?>>?
    fun queryPin(requestQueryPinWalletDTO: RequestQueryPinWalletDTO): Observable<ResponseQueryPinDTO>?
    fun activatePin(
        requestActivatePin: RequestVirtualWalletActivatePinDTO,
        isRetry: Boolean,
        transactionType: String?
    ): Observable<ResponseActivatePinDTO>?

    fun print(printModel: VirtualWalletPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?)
}

@Singleton
class VirtualWalletRepository : BaseRepository(), IVirtualWalletRepository {


    @Inject
    lateinit var virtualWalletPrint: IVirtualWalletPrint

    init {

        DaggerVirtualWalletRepositoryComponent
            .builder()
            .printVirtualWalletModule(PrintVirtualWalletModule())
            .build().inject(this)
    }

    override fun getProductParameters(): Observable<List<ProductParameterEntityRoom?>>? {
        val productInfo = PagaTodoApplication.getDatabaseInstance()?.productDao()
            ?.getProductInfo(R_string(R.string.code_product_virtual_wallet))
        return Observable.just(productInfo?.productParameters)
    }

    override fun queryPin(requestQueryPinWalletDTO: RequestQueryPinWalletDTO): Observable<ResponseQueryPinDTO>? {
        return ApiFactory.build()?.queryPinVirtualWallet(requestQueryPinWalletDTO)
    }

    override fun activatePin(
        requestActivatePin: RequestVirtualWalletActivatePinDTO,
        isRetry: Boolean,
        transactionType: String?
    ): Observable<ResponseActivatePinDTO>? {
        return ApiFactory.build()?.activatePinVirtualWallet(requestActivatePin)
            ?.onErrorResumeNext { throwable: Throwable ->
                if (throwable is SocketTimeoutException) {
                    requestActivatePin.transactionType = TransactionType.RETRY.rawValue
                    ApiFactory
                        .build(StorageUtil.getTimeoutRetry())
                        ?.activatePinVirtualWallet(requestActivatePin)
                        ?.retry(StorageUtil.getRetryCount())
                } else {
                    Observable.error(throwable)
                }
            }
    }

    override fun print(printModel: VirtualWalletPrintModel, callback: (printerStatus: PrinterStatus) -> Unit?) {
        virtualWalletPrint.print(printModel, callback)
    }
}