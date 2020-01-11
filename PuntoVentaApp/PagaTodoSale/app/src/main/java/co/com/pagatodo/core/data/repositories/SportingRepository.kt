package co.com.pagatodo.core.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import co.com.pagatodo.core.PagaTodoApplication
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.database.entities.ProductParameterEntityRoom
import co.com.pagatodo.core.data.dto.request.RequestGeneralParametersDTO
import co.com.pagatodo.core.data.dto.request.RequestSellSportingBetDTO
import co.com.pagatodo.core.data.dto.response.ResponseSellSportingBetDTO
import co.com.pagatodo.core.data.dto.response.ResponseSportingsDTO
import co.com.pagatodo.core.data.model.TransactionType
import co.com.pagatodo.core.data.model.print.SportingPrintModel
import co.com.pagatodo.core.data.print.ISportingPrint
import co.com.pagatodo.core.data.print.PrinterStatus
import co.com.pagatodo.core.di.print.PrintSportingModule
import co.com.pagatodo.core.di.repository.DaggerSportingRepositoryComponent
import co.com.pagatodo.core.network.ApiFactory
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.util.StorageUtil
import io.reactivex.Observable
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SportingRepository: BaseRepository(),
    ISportingRepository {

    @Inject
    lateinit var sportingPrint: ISportingPrint

    init {
        DaggerSportingRepositoryComponent
            .builder()
            .printSportingModule(PrintSportingModule())
            .build().inject(this)
    }

    override fun getProductParametersForSportingsRoom(): Observable<List<ProductParameterEntityRoom>>? {
        val data = mutableListOf<ProductParameterEntityRoom>()
        val productCode = getPreference<String>(R_string(R.string.code_product_sportings))
        val nameParameter = R_string(R.string.name_column_product_code)
        val product = PagaTodoApplication.getDatabaseInstance()?.productDao()?.getAllByCode(productCode)
        product?.productParameters?.forEach { itl ->
            itl?.let {
                data.add(it)
            }
        }
        return Observable.create {
            it.onNext(data)
            it.onComplete()
        }
    }

    override fun getParameters(): Observable<ResponseSportingsDTO>? {
        return ApiFactory.build()?.getSportingParameters(RequestGeneralParametersDTO().apply {
            userType = getPreference(R_string(R.string.shared_key_user_type))
            sellerCode = getPreference(R_string(R.string.shared_key_seller_code))
            canalId = getPreference(R_string(R.string.shared_key_canal_id))
            terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
        })
    }

    override fun sellSportingBet(requestDTO: RequestSellSportingBetDTO, isRetry: Boolean, transactionType: String?): Observable<ResponseSellSportingBetDTO>? {

        transactionType?.let {
            requestDTO.transactionType = it
        }

        if (!isRetry) {
            return ApiFactory.build()?.sellSportinBet(requestDTO)
        }

        return ApiFactory.build()?.sellSportinBet(requestDTO)
            ?.onErrorResumeNext { throwable: Throwable ->
                if (throwable is SocketTimeoutException) {
                    requestDTO.transactionType = TransactionType.RETRY.rawValue
                    ApiFactory
                        .build(StorageUtil.getTimeoutRetry())
                        ?.sellSportinBet(requestDTO)
                        ?.retry(StorageUtil.getRetryCount())
                } else {
                    Observable.error(throwable)
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun print(sportingModel: SportingPrintModel, isRetry: Boolean, callback: (printerStatus: PrinterStatus) -> Unit?) {
        sportingModel.sellerCode = getSellerCode()
        sportingPrint.print(sportingModel, isRetry, callback)
    }
}