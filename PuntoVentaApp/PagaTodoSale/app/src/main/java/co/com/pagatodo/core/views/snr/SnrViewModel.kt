package co.com.pagatodo.core.views.soat

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.dto.response.ResponseQuerySnrConceptsDTO
import co.com.pagatodo.core.data.dto.response.ResponseQuerySnrGetOfficesDTO
import co.com.pagatodo.core.data.dto.response.ResponseQuerySnrGetParametersDTO
import co.com.pagatodo.core.data.dto.response.ResponseQuerySnrMakeCollectionsDTO
import co.com.pagatodo.core.data.interactors.ISnrInteractor
import co.com.pagatodo.core.di.DaggerSnrComponent
import co.com.pagatodo.core.di.SnrModule
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.views.SingleLiveEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnrViewModel: ViewModel() {

    @Inject
    internal lateinit var snrInteractor: ISnrInteractor

    sealed class ViewEvent {
        class ResponseError(val errorMessage: String?): ViewEvent()
        class ResponseSuccess(val successMessage: String?): ViewEvent()
        class ResponseSuccessSnrConcepts(val responseQuerySnrConceptsDTO: ResponseQuerySnrConceptsDTO?): ViewEvent()
        class ResponseSuccessSnrGetOfficesDTO(val responseQuerySnrGetOfficesDTO: ResponseQuerySnrGetOfficesDTO?): ViewEvent()
        class ResponseSuccessSnrGetParameters(val responseQuerySnrGetParametersDTO: ResponseQuerySnrGetParametersDTO?): ViewEvent()
        class ResponseSuccessMakeSnrCollections(val responseQuerySnrMakeCollectionsDTO: ResponseQuerySnrMakeCollectionsDTO?): ViewEvent()
    }

    var singleLiveEvent: SingleLiveEvent<ViewEvent> =
        SingleLiveEvent()

    init {
        DaggerSnrComponent
            .builder()
            .snrModule(SnrModule())
            .build()
            .inject(this)
    }

    @SuppressLint("CheckResult")
    fun querySnrConcepts(){

        snrInteractor.querySnrConcepts()?.subscribe({

            if (it.isSuccess!!) {
                singleLiveEvent.value = ViewEvent.ResponseSuccessSnrConcepts(it)
            } else {
                singleLiveEvent.value = ViewEvent.ResponseError(it.message)
            }

        }, {
            singleLiveEvent.value = ViewEvent.ResponseError(RUtil.R_string(R.string.message_no_network))
        })

    }

    @SuppressLint("CheckResult")
    fun querySnrOffices(){

        snrInteractor.querySnrOffices()?.subscribe({

            if (it.isSuccess!!) {
                singleLiveEvent.value = ViewEvent.ResponseSuccessSnrGetOfficesDTO(it)
            } else {
                singleLiveEvent.value = ViewEvent.ResponseError(it.message)
            }

        }, {
            singleLiveEvent.value = ViewEvent.ResponseError(RUtil.R_string(R.string.message_no_network))
        })

    }

    @SuppressLint("CheckResult")
    fun querySnrGetParameters(){

        snrInteractor.querySnrGetParameters()?.subscribe({

            if (it.isSuccess!!) {
                singleLiveEvent.value = ViewEvent.ResponseSuccessSnrGetParameters(it)
            } else {
                singleLiveEvent.value = ViewEvent.ResponseError(RUtil.R_string(R.string.message_no_network))
            }

        }, {
            singleLiveEvent.value = ViewEvent.ResponseError(RUtil.R_string(R.string.message_no_network))
        })

    }

    @SuppressLint("CheckResult")
    fun makeSnrCollections(){

        snrInteractor.makeSnrCollections()?.subscribe({

            if (it.isSuccess!!) {
                singleLiveEvent.value = ViewEvent.ResponseSuccessMakeSnrCollections(it)
            } else {
                singleLiveEvent.value = ViewEvent.ResponseError(it.message)
            }

        }, {
            singleLiveEvent.value = ViewEvent.ResponseError(RUtil.R_string(R.string.message_no_network))
        })

    }

}