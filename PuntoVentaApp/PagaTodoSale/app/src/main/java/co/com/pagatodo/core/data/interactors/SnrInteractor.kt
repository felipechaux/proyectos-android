package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.request.*
import co.com.pagatodo.core.data.dto.response.ResponseQuerySnrConceptsDTO
import co.com.pagatodo.core.data.dto.response.ResponseQuerySnrGetOfficesDTO
import co.com.pagatodo.core.data.dto.response.ResponseQuerySnrGetParametersDTO
import co.com.pagatodo.core.data.dto.response.ResponseQuerySnrMakeCollectionsDTO
import co.com.pagatodo.core.data.repositories.ISnrRepository
import co.com.pagatodo.core.di.DaggerSnrComponent
import co.com.pagatodo.core.di.SnrModule
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

interface ISnrInteractor {

    fun querySnrConcepts(): Observable<ResponseQuerySnrConceptsDTO>?

    fun querySnrOffices(): Observable<ResponseQuerySnrGetOfficesDTO>?

    fun querySnrGetParameters(): Observable<ResponseQuerySnrGetParametersDTO>?

    fun makeSnrCollections(): Observable<ResponseQuerySnrMakeCollectionsDTO>?

}

@Singleton
class SnrInteractor : ISnrInteractor {

    @Inject
    lateinit var snrRepository: ISnrRepository

    init {
        DaggerSnrComponent
            .builder()
            .snrModule(SnrModule())
            .build()
            .inject(this)
    }

    constructor()

    constructor(snrRepository: ISnrRepository) {
        this.snrRepository = snrRepository
    }

    override fun querySnrConcepts(): Observable<ResponseQuerySnrConceptsDTO>? {
        val requestQuerySnrConceptsDTO = RequestQuerySnrConceptsDTO().apply {
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
        }

        return snrRepository
            .querySnrConcepts(requestQuerySnrConceptsDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun querySnrOffices(): Observable<ResponseQuerySnrGetOfficesDTO>? {
        val requestQuerySnrGetOfficesDTO = RequestQuerySnrGetOfficesDTO().apply {
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
        }

        return snrRepository
            .querySnrOffices(requestQuerySnrGetOfficesDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun querySnrGetParameters(): Observable<ResponseQuerySnrGetParametersDTO>? {
        val requestQuerySnrGetParametersDTO = RequestQuerySnrGetParametersDTO().apply {
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
        }

        return snrRepository
            .querySnrGetParameters(requestQuerySnrGetParametersDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

    override fun makeSnrCollections(): Observable<ResponseQuerySnrMakeCollectionsDTO>? {
        val requestQuerySnrMakeCollectionsDTO = RequestQuerySnrMakeCollectionsDTO().apply {
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = RUtil.R_string(R.string.code_product_giro)
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
        }

        return snrRepository
            .makeSnrCollections(requestQuerySnrMakeCollectionsDTO)
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                Observable.just(it)
            }
    }

}