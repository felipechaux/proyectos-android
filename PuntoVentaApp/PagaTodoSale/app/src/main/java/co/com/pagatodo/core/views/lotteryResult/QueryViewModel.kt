package co.com.pagatodo.core.views.lotteryResult

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.response.ResponseCheckResultLotteryDTO
import co.com.pagatodo.core.data.interactors.QueryInteractor
import co.com.pagatodo.core.data.model.LotteryResultModel
import co.com.pagatodo.core.di.DaggerQueryComponent
import co.com.pagatodo.core.di.QueryModule
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.SingleLiveEvent
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QueryViewModel: ViewModel() {

    sealed class ViewEvent {

        class ResponseError(val errorMessage: String?): ViewEvent()
        class LotteryResultQuery(val result: ResponseCheckResultLotteryDTO): ViewEvent()
        class DrawsLotteryResultQuery(val result: List<LotteryResultModel>): ViewEvent()
    }

    @Inject
    lateinit var queryInteractor: QueryInteractor

    var singleLiveEvent: SingleLiveEvent<ViewEvent> =
        SingleLiveEvent()

    init {
        DaggerQueryComponent
            .builder()
            .queryModule(QueryModule())
            .build().inject(this)
    }

    fun getAllLotteries(): List<String> {
        var list = arrayListOf<String>()
        list.add(R_string(R.string.placeholder_lottery_result_select_one))
        queryInteractor.getLotteries()?.forEach {
            list.add(it.code.plus("-").plus(it.name))
        }
        return list
    }

    @SuppressLint("CheckResult")
    fun dispatchQueryLotteryResult(drawDate: String, lotteryCode: String) {

        queryInteractor.checkVirtualLotteryResult(drawDate,lotteryCode)?.subscribe ({
                singleLiveEvent.value = ViewEvent.LotteryResultQuery(it)
            }, {
                if (it is ConnectException){
                    singleLiveEvent.value = ViewEvent.ResponseError(R_string(R.string.message_no_network))
                } else if (it !is SocketTimeoutException) {
                    singleLiveEvent.value = ViewEvent.ResponseError( R_string(R.string.message_error_transaction))
                }
            })
        }


    fun dispatchQueryLotteryResult(drawDate: String, lotteryCode: String? = null, number: String? = null) {
        queryInteractor.getQueryLotteryResult(drawDate, lotteryCode, number)?.let { observable ->
            observable.subscribe ({
                singleLiveEvent.value = ViewEvent.DrawsLotteryResultQuery(it)
            }, {
                if (it is ConnectException){
                    singleLiveEvent.value = ViewEvent.ResponseError(R_string(R.string.message_no_network))
                } else if (it !is SocketTimeoutException) {
                    singleLiveEvent.value = ViewEvent.ResponseError( R_string(R.string.message_error_transaction))
                }
            })
        }
    }


}