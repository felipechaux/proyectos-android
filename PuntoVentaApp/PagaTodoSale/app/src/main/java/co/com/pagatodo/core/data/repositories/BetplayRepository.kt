package co.com.pagatodo.core.data.repositories

import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.database.entities.ProductParameterEntityRoom
import co.com.pagatodo.core.data.dto.request.*
import co.com.pagatodo.core.data.dto.response.*
import co.com.pagatodo.core.data.model.TransactionType
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.data.print.RechargePrint
import co.com.pagatodo.core.di.BetplayModule
import co.com.pagatodo.core.di.DaggerBetplayComponent
import co.com.pagatodo.core.network.ApiFactory
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.StorageUtil
import io.reactivex.Observable
import java.net.SocketTimeoutException
import javax.inject.Singleton

@Singleton
class BetplayRepository: BaseRepository(), IBetplayRepository {

    init {
        DaggerBetplayComponent.builder().betplayModule(BetplayModule()).build().inject(this)
    }

    override fun payRecharge(requestDTO: RequestRechargeBetplayDTO, isRetry: Boolean, transactionType: String?): Observable<ResponseBetplayRechargeDTO>? {

        transactionType?.let {
            requestDTO.transactionType = it
        }

        if (!isRetry) {
            return ApiFactory.build()?.payRechargeBetplay(requestDTO)
        }

        return ApiFactory.build()?.payRechargeBetplay(requestDTO)
            ?.onErrorResumeNext { throwable: Throwable ->
                if (throwable is SocketTimeoutException) {
                    requestDTO.transactionType = TransactionType.RETRY.rawValue
                    ApiFactory
                        .build(StorageUtil.getTimeoutRetry())
                        ?.payRechargeBetplay(requestDTO)
                        ?.retry(StorageUtil.getRetryCount())
                } else {
                    Observable.error(throwable)
                }
            }
    }

    override fun isDocumentValid(requestRechargeBetplayDTO: RequestRechargeBetplayDTO): Observable<ResponseDocumentConsultDTO>? {
        return ApiFactory.build()?.isDocumentValidBetplay(requestRechargeBetplayDTO)
    }

    override fun getProductParametersForBetplayRoom(): Observable<List<ProductParameterEntityRoom>>? {
        val productCode = getPreference<String>(R_string(R.string.code_product_betplay))
        val productEntity= PagaTodoApplication.getDatabaseInstance()?.productDao()?.getProductInfo(productCode)
        productEntity?.productParameters?.let { params ->
            return Observable.create {
                it.onNext(params)
                it.onComplete()
            }
        } ?: run {
            return Observable.empty()
        }
    }

    override fun print(numberDoc: String, value: Int, stubs: String, digitChecking: String, isRetry: Boolean, typePrintText: String, isReprint: Boolean, isCollect: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        RechargePrint().printBetplay(numberDoc, value, getSellerCode(), digitChecking, stubs, isRetry, typePrintText, isReprint, isCollect, callback)

    }

    override fun reprintBetplay(
        requestDTO: RequestBetplayReprintDTO
    ): Observable<ResponseBetplayRechargeDTO>? {
        return ApiFactory.build()?.betplayReprint(requestDTO)
    }

    override fun quickBet(requestDTO: RequestPinQuickBetDTO): Observable<ResponsePinQuickBetDTO>? {
        return ApiFactory.build()?.betplayQuickBet(requestDTO)
    }

    override fun collectBetplay(requestDTO: RequestGeneratePinDTO): Observable<ResponseGeneratePinDTO>? {
        return ApiFactory.build()?.collectBetplay(requestDTO)
    }

    override fun checkOutBetplay(requestBetPlayCheckOut: RequestBetPlayCheckOut): Observable<ResponseBetPlayCheckOutDTO>? {
        return ApiFactory.build()?.checkOutBetplay(requestBetPlayCheckOut)
    }
}