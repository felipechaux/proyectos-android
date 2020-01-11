package co.com.pagatodo.core.views.querywinners

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.interactors.QueryWinnersInteractor
import co.com.pagatodo.core.di.DaggerQueryWinnersComponent
import co.com.pagatodo.core.di.QueryWinnersModule
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.SingleLiveEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QueryWinnersViewModel: ViewModel() {

    sealed class ViewEvent {
        class ResponseError(val errorMessage: String?, val codeMessage: String?): ViewEvent()
        class ResponseSuccess(val successMessage: String?, val codeMessage: String?): ViewEvent()
    }

    @Inject
    lateinit var interactor: QueryWinnersInteractor

    var singleLiveEvent: SingleLiveEvent<ViewEvent> = SingleLiveEvent()

    init {
        DaggerQueryWinnersComponent.builder().queryWinnersModule(QueryWinnersModule()).build().inject(this)
    }

    fun dispatchQueryWinners(serie1: String?, serie2: String?, uniqueSerial: String?){
        queryWinners(serie1, serie2, uniqueSerial)
    }
}

@SuppressLint("CheckResult")
private fun QueryWinnersViewModel.queryWinners(serie1: String?, serie2: String?, uniqueSerial: String?){
    interactor.queryWinners(serie1, serie2, uniqueSerial)?.subscribe ({
        if(it.isSuccess == true){
            if (it.responseCode.isNullOrEmpty()){
                singleLiveEvent.value = QueryWinnersViewModel.ViewEvent.ResponseSuccess(it.message, R_string(R.string.code_user_winner))
            }else{
                singleLiveEvent.value = QueryWinnersViewModel.ViewEvent.ResponseSuccess(it.message, it.responseCode)
            }
        }else{
            singleLiveEvent.value = QueryWinnersViewModel.ViewEvent.ResponseError(it.message,R_string(R.string.code_transaction_error))
        }
    },{
        singleLiveEvent.value = QueryWinnersViewModel.ViewEvent.ResponseError(it.localizedMessage,R_string(R.string.code_transaction_error))
    })
}