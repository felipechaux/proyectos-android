package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.request.RequestCardRechargeDTO
import co.com.pagatodo.core.data.dto.request.RequestQueryCardBlackDTO
import co.com.pagatodo.core.data.dto.request.RequestQueryInitDateDTO
import co.com.pagatodo.core.data.dto.request.RequestQueryInventoryDTO
import co.com.pagatodo.core.data.dto.response.ResponseCardRechargeDTO
import co.com.pagatodo.core.data.dto.response.ResponseQueryCardBlackDTO
import co.com.pagatodo.core.data.dto.response.ResponseQueryInitDateDTO
import co.com.pagatodo.core.data.dto.response.ResponseQueryInventoryDTO
import co.com.pagatodo.core.data.repositories.IRechargeSitpRepository
import co.com.pagatodo.core.data.repositories.RechargeSitpRepository
import co.com.pagatodo.core.di.DaggerRechargeSitpComponent

import co.com.pagatodo.core.di.RechargeSitpModule
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil


import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

interface IRechargeSitpInteractor {
    fun getCardRecharge(cardIdNumber: String, valueRecharge: Int): Observable<ResponseCardRechargeDTO>?
    fun getQueryInitData(): Observable<ResponseQueryInitDateDTO>?
    fun getQueryInventory(requestQueryInventoryDTO: RequestQueryInventoryDTO): Observable<ResponseQueryInventoryDTO>?
    fun getQueryCardBlack(requestQueryCardBlackDTO: RequestQueryCardBlackDTO): Observable<ResponseQueryCardBlackDTO>?
}

@Singleton
class RechargeSitpInteractor : IRechargeSitpInteractor {

    @Inject
    lateinit var repository: IRechargeSitpRepository

    init {
        DaggerRechargeSitpComponent
            .builder()
            .rechargeSitpModule(RechargeSitpModule())
            .build()
            .inject(this)
    }

    constructor()
    constructor(repository: RechargeSitpRepository) {
        this.repository = repository
    }

    override fun getCardRecharge(cardIdNumber: String, valueRecharge: Int): Observable<ResponseCardRechargeDTO>? {
        val requestCardRechargeDTO = RequestCardRechargeDTO().apply {

            this.channelId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.productCode = RUtil.R_string(R.string.code_product_virtual_soat)
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.series1 = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_current_serie1))
            this.series2 = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_current_serie2))
            this.cardNumber = cardIdNumber
            this.productCode = RUtil.R_string(R.string.recharge_sitp_product_code)
            this.value = valueRecharge
            this.operationType = RUtil.R_string(R.string.recharge_sitp_operation_type)
        }

        return repository
            .getCardRecharge(requestCardRechargeDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }

    }

    override fun getQueryInitData(): Observable<ResponseQueryInitDateDTO>? {
        val requestQueryInitDateDTO = RequestQueryInitDateDTO().apply {
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = RUtil.R_string(R.string.recharge_sitp_product_code)
            this.channelId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
        }

        return repository
            .getQueryInitData(requestQueryInitDateDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {

                repository.saveLocalSitpRecharge(it)

                Observable.just(it)
            }

    }

    override fun getQueryInventory(requestQueryInventoryDTO: RequestQueryInventoryDTO): Observable<ResponseQueryInventoryDTO>? {
        val requestQueryInventoryDTO = RequestQueryInventoryDTO().apply {
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.recharge_sitp_product_code))
            this.channelId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.recharge_sitp_terminal_code))
            this.terminalCode =
                SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.recharge_sitp_terminal_code))
        }

        return repository
            .getQueryInventory(requestQueryInventoryDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun getQueryCardBlack(requestQueryCardBlackDTO: RequestQueryCardBlackDTO): Observable<ResponseQueryCardBlackDTO>? {

        val requestQueryCardBlackDTO = RequestQueryCardBlackDTO().apply {
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.recharge_sitp_product_code))
            this.channelId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.recharge_sitp_terminal_code))
            this.terminalCode =
                SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.recharge_sitp_terminal_code))
            this.cardNumber = ""
        }

        return repository
            .getQueryCardBlack(requestQueryCardBlackDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

}