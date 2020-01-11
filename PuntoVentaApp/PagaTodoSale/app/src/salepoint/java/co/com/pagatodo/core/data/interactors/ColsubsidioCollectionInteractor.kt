package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.request.RequestColsubsidioDTO
import co.com.pagatodo.core.data.dto.RequestColsubsidioGetInitialDataDTO
import co.com.pagatodo.core.data.dto.request.RequestColsubsidioCollectObligationDTO
import co.com.pagatodo.core.data.dto.response.*
import co.com.pagatodo.core.data.repositories.IColsubsidioCollectionRepository
import co.com.pagatodo.core.di.ColsubsidioModule
import co.com.pagatodo.core.di.DaggerColsubsidioComponent
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

interface IColsubsidioCollectionInteractor {

    fun queryColsubsidioGetInitialData(): Observable<ResponseColsubsidioGetInitialDataDTO>?
    fun queryColsubsidioGetPaymentMethods(): Observable<ResponseColsubsidioGetPaymentMethodsDTO>?
    fun queryColsubsidioGetProducts(): Observable<ResponseColsubsidioGetProductsDTO>?
    fun queryColsubsidioGetBumperProducts(): Observable<ResponseColsubsidioBumperProductsDTO>?
    fun queryColsubsidioCollectObligation(): Observable<ResponseColsubsidioCollectObligationDTO>?

}

class ColsubsidioCollectionInteractor : IColsubsidioCollectionInteractor {

    @Inject
    lateinit var colsubsidioCollectionRepository: IColsubsidioCollectionRepository

    init {
        DaggerColsubsidioComponent
            .builder()
            .colsubsidioModule(ColsubsidioModule())
            .build()
            .inject(this)
    }

    constructor()

    constructor(colsubsidioCollectionRepository: IColsubsidioCollectionRepository) {
        this.colsubsidioCollectionRepository = colsubsidioCollectionRepository
    }

    override fun queryColsubsidioGetInitialData(): Observable<ResponseColsubsidioGetInitialDataDTO>? {
        val requestColsubsidioGetInitialDataDTO = RequestColsubsidioGetInitialDataDTO().apply {
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
        }

        return colsubsidioCollectionRepository
            .queryColsubsidioGetInitialData(requestColsubsidioGetInitialDataDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun queryColsubsidioGetPaymentMethods(): Observable<ResponseColsubsidioGetPaymentMethodsDTO>? {
        val requestColsubsidioDTO = RequestColsubsidioDTO().apply {
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
        }

        return colsubsidioCollectionRepository
            .queryColsubsidioGetPaymentMethods(requestColsubsidioDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun queryColsubsidioGetProducts(): Observable<ResponseColsubsidioGetProductsDTO>? {
        val requestColsubsidioDTO = RequestColsubsidioDTO().apply {
            this.channelId= SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = RUtil.R_string(R.string.code_product_colsubsidio_collection)
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
        }

        return colsubsidioCollectionRepository
            .queryColsubsidioGetProducts(requestColsubsidioDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun queryColsubsidioGetBumperProducts(): Observable<ResponseColsubsidioBumperProductsDTO>? {
        val requestColsubsidioDTO = RequestColsubsidioDTO().apply {
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
        }

        return colsubsidioCollectionRepository
            .queryColsubsidioGetBumperProducts(requestColsubsidioDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun queryColsubsidioCollectObligation(): Observable<ResponseColsubsidioCollectObligationDTO>? {
        val requestColsubsidioCollectObligationDTO = RequestColsubsidioCollectObligationDTO().apply {
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
        }

        return colsubsidioCollectionRepository
            .queryColsubsidioCollectObligation(requestColsubsidioCollectObligationDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }
}