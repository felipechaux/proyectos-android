package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.request.RequestQueryWinnersDTO
import co.com.pagatodo.core.data.model.QueryWinnersResponseModel
import co.com.pagatodo.core.data.repositories.IQueryWinnersRespository
import co.com.pagatodo.core.di.DaggerQueryWinnersComponent
import co.com.pagatodo.core.di.QueryWinnersModule
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil
import dagger.internal.DaggerCollections
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QueryWinnersInteractor {

    @Inject
    lateinit var repository: IQueryWinnersRespository

    init {
        DaggerQueryWinnersComponent
            .builder()
            .queryWinnersModule(QueryWinnersModule())
            .build().inject(this)
    }

    fun queryWinners(serie1: String?, serie2: String?, uniqueSerial: String?): Observable<QueryWinnersResponseModel>? {
        val request = RequestQueryWinnersDTO().apply {
            channelId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
            this.serie1 = serie1
            this.serie2 = serie2
            this.uniqueSerial = uniqueSerial
        }

        return repository
            .queryWinners(request)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {
                val response = QueryWinnersResponseModel().apply {
                    this.serie1 = it.serie1
                    this.serie2 = it.serie2
                    this.uniqueSerial = it.uniqueSerial
                    payValue = it.payValue
                    prizeValue = it.prizeValue
                    retentionValue = it.retentionValue
                    responseCode = it.responseCode
                    isSuccess = it.isSuccess
                    transactionDate = it.transactionDate
                    transactionHour = it.transactionHour
                    message = it.message
                    valueMegapleno = it.valueMegapleno
                    valueSuperpleno = it.valueSuperpleno
                    valueDirecto = it.valueDirecto
                    valueCombinado = it.valueCombinado
                    valuePata = it.valuePata
                    valueUna = it.valueUna
                }
                Observable.just(response)
            }
    }
}