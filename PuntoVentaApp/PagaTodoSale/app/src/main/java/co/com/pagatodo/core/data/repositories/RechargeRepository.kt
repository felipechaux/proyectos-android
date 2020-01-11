package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.database.DBHelperOperations
import co.com.pagatodo.core.data.database.DBHelperResponse
import co.com.pagatodo.core.data.database.entities.OperatorEntityRoom
import co.com.pagatodo.core.data.database.entities.ProductParameterEntityRoom
import co.com.pagatodo.core.data.dto.request.RequestRechargeDTO
import co.com.pagatodo.core.data.dto.request.RequestRechargeHistoryDTO
import co.com.pagatodo.core.data.dto.response.ResponseRechargeDTO
import co.com.pagatodo.core.data.dto.response.ResponseRechargeHistoryDTO
import co.com.pagatodo.core.data.interactors.ILocalInteractor
import co.com.pagatodo.core.data.model.TransactionType
import co.com.pagatodo.core.data.print.IRechargePrint
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.print.PrintRechargeModule
import co.com.pagatodo.core.di.repository.DaggerRechargeRepositoryComponent
import co.com.pagatodo.core.network.ApiFactory
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.StorageUtil
import io.reactivex.Observable
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RechargeRepository: BaseRepository(),
    IRechargeRepository {

    @Inject
    lateinit var rechargePrint: IRechargePrint

    @Inject
    lateinit var localInteractor: ILocalInteractor

    init {

        DaggerRechargeRepositoryComponent
            .builder()
            .printRechargeModule(PrintRechargeModule())
            .build().inject(this)
    }

    override fun getOperatorsForRechargeRoom(): Observable<List<OperatorEntityRoom>>? {
        val data = PagaTodoApplication.getDatabaseInstance()?.operatorDao()?.getOperators()
        return Observable.just(data)
    }

    override fun getProductParametersRooms(): Observable<List<ProductParameterEntityRoom>>? {
        val data = mutableListOf<ProductParameterEntityRoom>()
        val productCode = R_string(R.string.code_product_recharge)
        PagaTodoApplication.getDatabaseInstance()?.productDao()?.getProductByCode(productCode)?.let { product ->
            product?.productParameters?.forEach { itl ->
                itl?.let {
                    data.add(it)
                }
            }
        }
        return Observable.create {
            it.onNext(data)
            it.onComplete()
        }
    }

    override fun saveOperatorsRoom(operatorsEntity: List<OperatorEntityRoom>): Observable<DBHelperResponse>? {
        val response = DBHelperResponse().apply {
            status = true
            operation = DBHelperOperations.insertRows
        }

        val dbInstance = PagaTodoApplication.getDatabaseInstance()
        try {
            dbInstance?.operatorDao()?.insertWithPackages(operatorsEntity)
        }
        catch (e: Exception) {
            response.status = false
        }

        return Observable.just(response)
    }

    override fun dispatchRecharge(requestRechargeDTO: RequestRechargeDTO, isRetry: Boolean, transactionType: String?): Observable<ResponseRechargeDTO>? {
        transactionType?.let {
            requestRechargeDTO.transactionType = it
        }

        if (!isRetry) {
            return ApiFactory.build()?.dispatchRecharge(requestRechargeDTO)
        }

        return ApiFactory.build()?.dispatchRecharge(requestRechargeDTO)
            ?.onErrorResumeNext { throwable: Throwable ->
                if (throwable is SocketTimeoutException) {
                    requestRechargeDTO.transactionType = TransactionType.RETRY.rawValue
                    ApiFactory
                        .build(StorageUtil.getTimeoutRetry())
                        ?.dispatchRecharge(requestRechargeDTO)
                        ?.retry(StorageUtil.getRetryCount())
                } else {
                    Observable.error(throwable)
                }
            }

    }

    override fun getRechargeHistory(requestRechargeHistoryDTO: RequestRechargeHistoryDTO): Observable<ResponseRechargeHistoryDTO>? {
        return ApiFactory.build()?.historyRecharge(requestRechargeHistoryDTO)
    }

    override fun printRecharge(stub: String, billNumber: String, transactionDate: String, transactionHour: String, number:String, value:String, billPrefix: String, billMessage: String, isRetry: Boolean,headerGelsa:String, decriptionPackage:String,operationName :String, callback: (printerStatus: PrinterStatus) -> Unit?) {

        rechargePrint.printRecharge(stub, billNumber, transactionDate, transactionHour, number, value, billPrefix, billMessage, isRetry,headerGelsa, decriptionPackage,operationName, callback)

    }
}