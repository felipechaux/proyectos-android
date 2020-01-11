package co.com.pagatodo.core.data.interactors

import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.database.entities.LotteryEntityRoom
import co.com.pagatodo.core.data.dto.request.RequestCheckResultLotteryDTO
import co.com.pagatodo.core.data.dto.response.ResponseCheckResultLotteryDTO
import co.com.pagatodo.core.data.model.LotteryModel
import co.com.pagatodo.core.data.model.LotteryResultModel
import co.com.pagatodo.core.data.repositories.IQueryRepository
import co.com.pagatodo.core.data.repositories.LotteryRepository
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.SharedPreferencesUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

@Singleton
class QueryInteractor {

    private lateinit var queryRepository: IQueryRepository
    var lotteryRepository: LotteryRepository

    constructor(queryRepository: IQueryRepository, lotteryRepository: LotteryRepository){
        this.queryRepository = queryRepository
        this.lotteryRepository = lotteryRepository
    }

    fun getLotteries(): List<LotteryEntityRoom>? {
        return lotteryRepository.getAllLotteries()
    }

    fun getQueryLotteryResult(drawDate: String, lotteryCode: String? = null, number: String? = null): Observable<List<LotteryResultModel>>? {
        queryRepository
            .getLotteryResult(drawDate, lotteryCode, number)?.let { observable ->
            return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                ?.flatMap {
                    val results = arrayListOf<LotteryResultModel>()
                    it.results?.forEach { dto ->
                        results.add(
                            LotteryResultModel().apply {
                                this.date = dto.date
                                this.lotteryCode = dto.lotteryCode
                                this.lotteryName = dto.lotteryName
                                this.number = dto.number
                                this.serie = dto.serie
                                this.shortLotteryName = dto.shortLotteryName
                            }
                        )
                    }
                    Observable.just(results)
                }
        }
        return null
    }

    fun checkVirtualLotteryResult(mDrawDate: String, mLotteryCode: String): Observable<ResponseCheckResultLotteryDTO>? {

        val requestCheckResultLotteryDTO = RequestCheckResultLotteryDTO().apply {

            this.drawDate = mDrawDate
            this.lotteryCode = mLotteryCode
            this.canalId = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_canal_id))
            this.terminalCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_terminal_code))
            this.sellerCode = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_seller_code))
            this.productCode = RUtil.R_string(R.string.code_product_lottery)
            this.userType = SharedPreferencesUtil.getPreference(RUtil.R_string(R.string.shared_key_user_type))
        }

        return queryRepository
            .checkVirtualLotteryResult(requestCheckResultLotteryDTO)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.flatMap {


                Observable.just(it)
            }
    }

}
