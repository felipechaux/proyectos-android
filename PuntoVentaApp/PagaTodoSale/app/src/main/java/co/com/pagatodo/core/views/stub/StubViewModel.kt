package co.com.pagatodo.core.views.stub

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.com.pagatodo.core.R
import co.com.pagatodo.core.data.interactors.IAuthInteractor
import co.com.pagatodo.core.data.interactors.IUtilInteractor
import co.com.pagatodo.core.data.model.StubModel
import co.com.pagatodo.core.data.model.request.RequestUtilModel
import co.com.pagatodo.core.data.repositories.local.ILocalRepository
import co.com.pagatodo.core.di.*
import co.com.pagatodo.core.util.RUtil
import co.com.pagatodo.core.util.RUtil.Companion.R_string
import co.com.pagatodo.core.views.SingleLiveEvent
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StubViewModel: ViewModel() {

    @Inject
    internal lateinit var authInteractor: IAuthInteractor
    @Inject
    internal lateinit var localRepository: ILocalRepository
    @Inject
    internal lateinit var utilInteractor: IUtilInteractor

    lateinit var stubLiveData: MutableLiveData<List<StubModel>>

    sealed class ViewEvent {
        class UpdateSuccess(val successMessage: String): ViewEvent()
        class SaveMenus(): ViewEvent()
        class UpdateError(val errorMessage: String): ViewEvent()
        class ResponseError(val errorMessage: String?): ViewEvent()
        class ResponseSuccess(val successMessage: String?): ViewEvent()
    }
    var singleLiveEvent: SingleLiveEvent<ViewEvent> = SingleLiveEvent()

    init {
        DaggerStubComponent.builder()
            .authModule(AuthModule())
            .localModule(LocalModule())
            .utilModule(UtilModule())
            .build()
            .inject(this)

        if (!::stubLiveData.isInitialized) {
            stubLiveData = MutableLiveData()
        }
    }

    @SuppressLint("CheckResult")
    fun loadAllStubs() {
        authInteractor.getStubs()?.subscribe ({
            stubLiveData.postValue(it)
        },{
            if (it is ConnectException){
                singleLiveEvent.postValue(ViewEvent.UpdateError(R_string(R.string.message_no_network)))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.postValue(ViewEvent.UpdateError( RUtil.R_string(R.string.message_error_stub)))
            }
        })
    }

    fun saveCurrentStubSeries(serie1: String, serie2: String, isValidWithStubList: Boolean = true): Boolean {
        return localRepository.saveCurrentStubSeries(serie1, serie2, isValidWithStubList)
    }

    @SuppressLint("CheckResult")
    fun updateStubInServer(requestModel: RequestUtilModel) {
        utilInteractor.updateStubInServer(requestModel)?.subscribe({
            if (it.isSuccess == true) {
                getMenus()
            }
            else {
                singleLiveEvent.value = ViewEvent.UpdateError( RUtil.R_string(R.string.message_error_stub))
            }
        }, {
            if (it is ConnectException){
                singleLiveEvent.value = ViewEvent.UpdateError(R_string(R.string.message_no_network))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.value = ViewEvent.UpdateError( RUtil.R_string(R.string.message_error_stub))
            }
        })
    }

    @SuppressLint("CheckResult")
    fun getMenus(){
        utilInteractor.getMenus()?.subscribe({
            if (it.isSuccess == true) {
                utilInteractor.saveMenusInLocal(it.menus)
                singleLiveEvent.value = ViewEvent.SaveMenus()
            }
            else {
                singleLiveEvent.value = ViewEvent.UpdateError(it.message ?: RUtil.R_string(R.string.message_error_stub))
            }
        },{
            if (it is ConnectException){
                singleLiveEvent.value = ViewEvent.UpdateError(R_string(R.string.message_no_network))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.value = ViewEvent.UpdateError( RUtil.R_string(R.string.message_error_stub))
            }
        })
    }

    @SuppressLint("CheckResult")
    fun logout() {
        authInteractor.logout()?.subscribe ({
            if(it.isSuccess) {
                singleLiveEvent.value = ViewEvent.ResponseSuccess(it.message.toString())
            } else {
                singleLiveEvent.value = ViewEvent.ResponseError(it.message.toString())
            }
        }, {
            if (it is ConnectException){
                singleLiveEvent.value = ViewEvent.ResponseError(R_string(R.string.message_no_network))
            } else if (it !is SocketTimeoutException) {
                singleLiveEvent.value = ViewEvent.ResponseError(R_string(R.string.message_error_transaction))
            }
        })
    }
}