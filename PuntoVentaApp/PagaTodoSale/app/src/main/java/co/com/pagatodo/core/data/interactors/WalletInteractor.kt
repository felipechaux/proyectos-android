package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.util.SharedPreferencesUtil.Companion.getPreference
import co.com.pagatodo.core.data.model.response.ResponseWalletModel
import co.com.pagatodo.core.data.repositories.IWalletRepository
import co.com.pagatodo.core.data.dto.request.RequestWalletDTO
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import io.reactivex.android.schedulers.AndroidSchedulers
import co.com.pagatodo.core.di.DaggerWalletComponent
import co.com.pagatodo.core.di.WalletModule
import io.reactivex.schedulers.Schedulers
import io.reactivex.Observable
import co.com.pagatodo.core.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletInteractor {

    @Inject lateinit var walletRepository: IWalletRepository

    init {
        DaggerWalletComponent.builder().walletModule(WalletModule()).build().inject(this)
    }

    fun getWallet(dateQuery: String, sellerCode: String): Observable<ResponseWalletModel>? {
        val requestWalletDTO: RequestWalletDTO = createRequestDTO(dateQuery, sellerCode)
        return walletRepository
            .getWallet(requestWalletDTO)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = ResponseWalletModel().apply {
                    this.codeResponse = it.codeResponse
                    this.success = it.success
                    this.transactionDate = it.transactionDate
                    this.transactionHour = it.transactionHour
                    this.sellerName = it.sellerName
                    this.areaBalance = it.balanceArea
                    this.sellingArea = it.sellingArea
                    this.collectionArea = it.recaudoArea
                    this.sellerSale = it.sellerSale
                    this.sellerCollection = it.sellerRecaudo
                    this.sellerBalance = it.sellerBalance
                    this.area = it.area
                }
                Observable.just(response)
            }
    }

    private fun createRequestDTO(dateQuery: String, sellerCode: String): RequestWalletDTO {
        return RequestWalletDTO().apply {
            this.idChannel = getPreference(R_string(R.string.shared_key_canal_id))
            this.terminalCode = getPreference(R_string(R.string.shared_key_terminal_code))
            this.userType = getPreference(R_string(R.string.shared_key_user_type))
            this.sellerCode = sellerCode
            this.dateQuery = dateQuery
        }
    }

}